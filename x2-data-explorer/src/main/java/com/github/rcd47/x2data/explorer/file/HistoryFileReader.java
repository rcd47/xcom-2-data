package com.github.rcd47.x2data.explorer.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import com.github.rcd47.x2data.explorer.prefs.script.ScriptPreferences;
import com.github.rcd47.x2data.lib.history.X2HistoryIndexEntry;
import com.github.rcd47.x2data.lib.history.X2HistoryReader;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

public class HistoryFileReader {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	private static final UnrealName PREV_FRAME_HIST_INDEX = new UnrealName("PreviousHistoryFrameIndex");
	private static final String PROBLEM_MODIFIED_OLD_STATE = """
			Found object of type %s with no ObjectID and PreviousHistoryFrameIndex pointing to the future (frame %d).
			This is caused by code modifying a state that has already been submitted instead of modifying a new state.""";
	
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
				List<HistoryFileProblem> problemsDetected = new ArrayList<>();
				var contextSummarizer = ScriptPreferences.CONTEXT_SUMMARY.getExecutable();
				var objectSummarizer = ScriptPreferences.STATE_OBJECT_SUMMARY.getExecutable();
				
				XComGameStateHistory history = historyIndex.mapObject(historyIndex.getEntry(0), null, NullXComObjectReferenceResolver.INSTANCE);
				int numFrames = history.History.size();
				boolean foundFirstFrame = historyIndex.isCreatedByWOTC();
				for (int i = 0; i < numFrames; i++) {
					var frameRef = history.History.get(i);
					if (!foundFirstFrame && frameRef.index() == -1) {
						// before WOTC, NumArchivedFrames did not exist and archived frames were represented by -1 in the History array
						continue;
					}
					XComGameState rawFrame = historyIndex.mapObject(
							historyIndex.getEntry(frameRef.index()), null, NullXComObjectReferenceResolver.INSTANCE);
					var parsedFrame = new HistoryFrame(rawFrame.HistoryIndex, rawFrame.TimeStamp);
					progressTextCallback.accept("Parsing history frame " + rawFrame.HistoryIndex);
					
					var contextEntry = historyIndex.getEntry(rawFrame.StateChangeContext.index());
					var contextVisitor = new GenericObjectVisitor(null);
					historyIndex.parseObject(contextEntry, contextVisitor);
					var parsedContext = new GameStateContext(
							contextVisitor.getRootObject(), parsedFrame, frames, contextSummarizer, problemsDetected);
					
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
						var stateObject = stateObjectVisitor.getRootObject();
						if (stateObject.properties.get(OBJECT_ID) == null &&
								(int) stateObject.properties.get(PREV_FRAME_HIST_INDEX) > rawFrame.HistoryIndex) {
							// object has no ID and previous frame index points to the future
							// this is a sign of https://github.com/rcd47/xcom-2-data/issues/2
							// note that in this situation, the previousVersionIndex above is -1
							// so we are not corrupting our tracking of any objects by doing parseObject()
							problemsDetected.add(new HistoryFileProblem(
									parsedFrame,
									null,
									null,
									String.format(
											PROBLEM_MODIFIED_OLD_STATE,
											stateObjectEntry.getType().getOriginal(),
											stateObject.properties.get(PREV_FRAME_HIST_INDEX))));
						} else {
							parsedObjects.put(stateObjectRef.index(), stateObject);
							new GameStateObject(stateObjects, stateObject, parsedFrame, objectSummarizer, problemsDetected); // adds itself to the map
						}
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
				
				return new HistoryFile(history, frames.values().stream().sorted().toList(), singletons, problemsDetected);
			}
		} finally {
			Files.deleteIfExists(decompressedFile);
		}
	}

}
