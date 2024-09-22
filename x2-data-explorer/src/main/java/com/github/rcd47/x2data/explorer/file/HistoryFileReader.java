package com.github.rcd47.x2data.explorer.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import com.github.rcd47.x2data.explorer.prefs.script.ScriptPreferences;
import com.github.rcd47.x2data.lib.history.X2HistoryIndexEntry;
import com.github.rcd47.x2data.lib.history.X2HistoryReader;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

public class HistoryFileReader {
	
	public HistoryFile read(FileChannel in, DoubleConsumer progressPercentCallback, Consumer<String> progressTextCallback) throws IOException {
		var decompressedFile = Files.createTempFile("x2hist", null);
		try (var decompressedIn = FileChannel.open(decompressedFile, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			progressTextCallback.accept("Decompressing file");
			var reader = new X2HistoryReader();
			reader.decompress(in, decompressedIn);
			progressTextCallback.accept("Building index");
			try (var historyIndex = reader.buildIndex(decompressedIn)) {
				Map<Integer, HistoryFrame> frames = new HashMap<>();
				Map<Integer, GenericObject> parsedObjects = new HashMap<>();
				Map<Integer, GameStateObject> stateObjects = new HashMap<>();
				Set<X2HistoryIndexEntry> singletonStates = new HashSet<>();
				var contextSummarizer = ScriptPreferences.CONTEXT_SUMMARY.getExecutable();
				var objectSummarizer = ScriptPreferences.STATE_OBJECT_SUMMARY.getExecutable();
				
				XComGameStateHistory history = historyIndex.mapObject(historyIndex.getEntry(0), null, NullXComObjectReferenceResolver.INSTANCE);
				int numFrames = history.History.size();
				for (int i = 0; i < numFrames; i++) {
					var frameRef = history.History.get(i);
					XComGameState rawFrame = historyIndex.mapObject(
							historyIndex.getEntry(frameRef.index()), null, NullXComObjectReferenceResolver.INSTANCE);
					var parsedFrame = new HistoryFrame(rawFrame.HistoryIndex, rawFrame.TimeStamp);
					progressTextCallback.accept("Parsing history frame " + rawFrame.HistoryIndex);
					
					var contextEntry = historyIndex.getEntry(rawFrame.StateChangeContext.index());
					var contextVisitor = new GenericObjectVisitor(null);
					historyIndex.parseObject(contextEntry, contextVisitor);
					var parsedContext = new GameStateContext(contextVisitor.getRootObject(), parsedFrame, frames, contextSummarizer);
					
					for (var stateObjectRef : rawFrame.GameStates) {
						var stateObjectEntry = historyIndex.getEntry(stateObjectRef.index());
						if (stateObjectEntry.isSingletonState()) {
							singletonStates.add(stateObjectEntry);
							continue;
						}
						
						var previousVersionIndex = stateObjectEntry.getPreviousVersionIndex();
						var previousVersion = previousVersionIndex == -1 ? null : parsedObjects.get(previousVersionIndex);
						var stateObjectVisitor = new GenericObjectVisitor(previousVersion);
						historyIndex.parseObject(stateObjectEntry, stateObjectVisitor);
						parsedObjects.put(stateObjectRef.index(), stateObjectVisitor.getRootObject());
						new GameStateObject(stateObjects, stateObjectVisitor.getRootObject(), parsedFrame, objectSummarizer); // adds itself to the map
					}
					
					parsedFrame.finish(parsedContext, Map.copyOf(stateObjects));
					frames.put(parsedFrame.getNumber(), parsedFrame);
					progressPercentCallback.accept(((double) i + 1) / numFrames);
				}
				
				var singletons = singletonStates
						.stream()
						.map(s -> {
							var stateObjectVisitor = new GenericObjectVisitor(null);
							try {
								historyIndex.parseObject(s, stateObjectVisitor);
							} catch (IOException e) {
								// should never happen
								throw new UncheckedIOException(e);
							}
							return new HistorySingletonObject(stateObjectVisitor.getRootObject());
						})
						.sorted((a, b) -> a.getType().compareTo(b.getType()))
						.toList();
				
				return new HistoryFile(history, frames.values().stream().sorted().toList(), singletons);
			}
		} finally {
			Files.deleteIfExists(decompressedFile);
		}
	}

}
