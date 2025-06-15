package com.github.rcd47.x2data.explorer.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
				XComGameStateHistory history = historyIndex.mapObject(historyIndex.getEntry(0), null, NullXComObjectReferenceResolver.INSTANCE);
				var frameRefs = history.History;
				var currentFrameNum = history.NumArchivedFrames + 1;
				if (!historyIndex.isCreatedByWOTC()) {
					// before WOTC, NumArchivedFrames did not exist and archived frames were represented by -1 in the History array
					for (int i = frameRefs.size() - 1; i >= 0; i--) {
						if (frameRefs.get(i).index() == -1) {
							frameRefs = frameRefs.subList(i + 1, frameRefs.size());
							currentFrameNum = i + 2;
							break;
						}
					}
				}
				
				// first pass to parse the state and detect singletons
				var numFrames = frameRefs.size();
				var rawFrames = new XComGameState[numFrames];
				var seenObjectIndexes = new HashSet<Integer>();
				var detectedSingletonTypes = new HashSet<UnrealName>();
				for (int i = 0; i < numFrames; i++) {
					progressTextCallback.accept("Parsing history frame " + currentFrameNum++);
					XComGameState rawFrame = historyIndex.mapObject(
							historyIndex.getEntry(frameRefs.get(i).index()), null, NullXComObjectReferenceResolver.INSTANCE);
					rawFrames[i] = rawFrame;
					for (var objRef : rawFrame.GameStates) {
						if (!seenObjectIndexes.add(objRef.index())) {
							// multiple frames pointing to same object index, so class must be a singleton
							// note that in strategy saves, two versions of a singleton are written
							// the first frame (archive frame) points to one version
							// all other frames point to the other version
							// this does not happen for tactical saves, where all frames point to a single version
							detectedSingletonTypes.add(historyIndex.getEntry(objRef.index()).getType());
						}
					}
				}
				
				// second pass to parse the state objects
				Map<Integer, HistoryFrame> frames = new LinkedHashMap<>();
				Map<Integer, GenericObject> parsedObjects = new HashMap<>();
				Map<Integer, GameStateObject> stateObjects = new HashMap<>();
				Map<X2HistoryIndexEntry, Integer> singletonStates = new HashMap<>();
				List<HistoryFileProblem> problemsDetected = new ArrayList<>();
				var contextSummarizer = ScriptPreferences.CONTEXT_SUMMARY.getExecutable();
				var objectSummarizer = ScriptPreferences.STATE_OBJECT_SUMMARY.getExecutable();
				for (int i = 0; i < numFrames; i++) {
					XComGameState rawFrame = rawFrames[i];
					var parsedFrame = new HistoryFrame(rawFrame.HistoryIndex, rawFrame.TimeStamp);
					progressTextCallback.accept("Parsing objects for history frame " + rawFrame.HistoryIndex);
					
					var contextEntry = historyIndex.getEntry(rawFrame.StateChangeContext.index());
					var contextVisitor = new GenericObjectVisitor(null);
					historyIndex.parseObject(contextEntry, contextVisitor);
					var parsedContext = new GameStateContext(
							contextEntry.getLength(), contextVisitor.getRootObject(), parsedFrame, frames, contextSummarizer, problemsDetected);
					
					for (var stateObjectRef : rawFrame.GameStates) {
						var stateObjectEntry = historyIndex.getEntry(stateObjectRef.index());
						if (detectedSingletonTypes.contains(stateObjectEntry.getType())) {
							singletonStates.putIfAbsent(stateObjectEntry, rawFrame.HistoryIndex);
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
							new GameStateObject(stateObjectEntry.getLength(), stateObjects, stateObject, parsedFrame, objectSummarizer, problemsDetected); // adds itself to the map
						}
					}
					
					parsedFrame.finish(parsedContext, Map.copyOf(stateObjects));
					frames.put(parsedFrame.getNumber(), parsedFrame);
					progressPercentCallback.accept(((double) i + 1) / numFrames);
				}
				
				progressTextCallback.accept("Parsing singletons");
				var singletons = singletonStates
						.entrySet()
						.stream()
						.map(entry -> {
							var key = entry.getKey();
							var stateObjectVisitor = new GenericObjectVisitor(null);
							try {
								historyIndex.parseObject(key, stateObjectVisitor);
							} catch (IOException e) {
								// should never happen
								throw new UncheckedIOException(e);
							}
							return new HistorySingletonObject(key.getLength(), entry.getValue(), stateObjectVisitor.getRootObject());
						})
						.sorted(Comparator
								.<HistorySingletonObject, UnrealName>comparing(s -> s.getType())
								.thenComparingInt(s -> s.getFirstFrame()))
						.toList();
				
				return new HistoryFile(history, List.copyOf(frames.values()), singletons, problemsDetected);
			}
		} finally {
			Files.deleteIfExists(decompressedFile);
		}
	}

}
