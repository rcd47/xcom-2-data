package com.github.rcd47.x2data.explorer.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import org.apache.commons.lang3.function.FailableRunnable;

import com.github.rcd47.x2data.explorer.file.data.PrimitiveInterner;
import com.github.rcd47.x2data.explorer.file.data.VersionedObjectVisitor;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedMap;
import com.github.rcd47.x2data.explorer.prefs.script.ScriptPreferences;
import com.github.rcd47.x2data.lib.history.X2HistoryIndex;
import com.github.rcd47.x2data.lib.history.X2HistoryIndexEntry;
import com.github.rcd47.x2data.lib.history.X2HistoryReader;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class HistoryFileReader {
	
	private static final double PROGRESS_BAR_PHASES = 4;
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	private static final UnrealName PREV_FRAME_HIST_INDEX = new UnrealName("PreviousHistoryFrameIndex");
	private static final String PROBLEM_MODIFIED_OLD_STATE = """
			Found object of type %s with no ObjectID and PreviousHistoryFrameIndex pointing to the future (frame %d).
			This is caused by code modifying a state that has already been submitted instead of modifying a new state.""";
	
	public HistoryFile read(FileChannel in, DoubleConsumer progressPercentCallback, Consumer<String> progressTextCallback) throws Exception {
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
				var numArchivedFrames = currentFrameNum - 1;
				
				// first pass to detect singletons and set up work queues
				var numFrames = frameRefs.size();
				var parsedFrames = new HistoryFrame[numFrames];
				var contextEntries = new X2HistoryIndexEntry[numFrames];
				var objectsChangedInFrame = new ArrayList<Set<GameStateObject>>(numFrames);
				var objectStateChains = new Int2ReferenceOpenHashMap<List<GameStateObjectState>>();
				var detectedSingletonTypes = new HashSet<UnrealName>();
				var objectTypes = new HashSet<UnrealName>();
				var contextTypes = new HashSet<UnrealName>();
				for (int i = 0; i < numFrames; i++) {
					progressTextCallback.accept("Inspecting history frame " + currentFrameNum++);
					XComGameState rawFrame = historyIndex.mapObject(
							historyIndex.getEntry(frameRefs.get(i).index()), null, NullXComObjectReferenceResolver.INSTANCE);
					parsedFrames[i] = new HistoryFrame(rawFrame.HistoryIndex, rawFrame.TimeStamp);
					contextEntries[i] = historyIndex.getEntry(rawFrame.StateChangeContext.index());
					contextTypes.add(contextEntries[i].getType());
					objectsChangedInFrame.add(ConcurrentHashMap.newKeySet());
					for (var objRef : rawFrame.GameStates) {
						var entryIndex = objRef.index();
						var fileEntry = historyIndex.getEntry(entryIndex);
						var prevIndex = fileEntry.getPreviousVersionIndex();
						List<GameStateObjectState> chain;
						if (prevIndex == -1) {
							if (objectStateChains.containsKey(entryIndex)) {
								// multiple frames pointing to same object index, so class must be a singleton
								// note that in strategy saves, two versions of a singleton are written
								// the first frame (archive frame) points to one version
								// all other frames point to the other version
								// this does not happen for tactical saves, where all frames point to a single version
								detectedSingletonTypes.add(fileEntry.getType());
								continue;
							}
							chain = new ArrayList<>();
							objectTypes.add(fileEntry.getType());
						} else {
							chain = objectStateChains.remove(prevIndex);
						}
						chain.add(new GameStateObjectState(fileEntry, parsedFrames[i]));
						objectStateChains.put(entryIndex, chain);
					}
					progressPercentCallback.accept((((double) i) / numFrames) / PROGRESS_BAR_PHASES);
				}
				
				// filter out singletons
				progressTextCallback.accept("Filtering singleton states");
				var singletonStates = new HashSet<GameStateObjectState>();
				var stateObjectQueue = new ArrayBlockingQueue<List<GameStateObjectState>>(objectStateChains.size());
				for (var chain : objectStateChains.values()) {
					var firstState = chain.getFirst();
					if (detectedSingletonTypes.contains(firstState.fileEntry.getType())) {
						singletonStates.add(firstState);
					} else {
						stateObjectQueue.add(chain);
					}
				}
				objectTypes.removeAll(detectedSingletonTypes);
				
				// parse state objects in parallel
				progressTextCallback.accept("Preparing to parse objects");
				var stateObjectQueueSize = stateObjectQueue.size();
				var objectCounter = new AtomicInteger(stateObjectQueueSize);
				var interner = new PrimitiveInterner();
				var problemsDetected = Collections.synchronizedList(new ArrayList<HistoryFileProblem>());
				readInParallel(
						"GSO Parser ",
						() -> readGameStateObjects(
								numArchivedFrames, historyIndex, objectsChangedInFrame, interner,
								problemsDetected, stateObjectQueue, objectCounter, progressTextCallback,
								progressPercentCallback, stateObjectQueueSize));
				
				// parse contexts in parallel
				progressTextCallback.accept("Preparing to parse contexts");
				objectCounter.set(numFrames);
				var contextArrayIndex = new AtomicInteger();
				readInParallel(
						"GSC Parser",
						() -> readGameStateContexts(
								contextArrayIndex, historyIndex, contextEntries, parsedFrames,
								numArchivedFrames, interner, problemsDetected, objectCounter,
								progressTextCallback, progressPercentCallback, numFrames));
				
				// finish building frames
				var stateObjectsInFrameCold = new Int2ReferenceOpenHashMap<GameStateObject>();
				var stateObjectsInFrameHot = new Int2ReferenceOpenHashMap<GameStateObject>();
				var removedObjectsInFrame = new IntArrayList();
				for (int i = 0; i < numFrames; i++) {
					removedObjectsInFrame.forEach(objId -> stateObjectsInFrameHot.put(objId, null));
					removedObjectsInFrame.clear();
					
					var frame = parsedFrames[i];
					progressTextCallback.accept("Building history frame " + frame.getNumber());
					
					var hotMapUpdates = objectsChangedInFrame.get(i);
					if (hotMapUpdates.isEmpty()) {
						frame.setObjectsHot(stateObjectsInFrameHot);
					} else {
						for (var changedObject : hotMapUpdates) {
							stateObjectsInFrameHot.put(changedObject.getObjectId(), changedObject);
							if (changedObject.isRemoved()) {
								removedObjectsInFrame.add(changedObject.getObjectId());
							}
						}
						if (stateObjectsInFrameHot.size() > 100) { // 100 is an arbitrary number I picked
							stateObjectsInFrameCold = HistoryFrame.combineMaps(stateObjectsInFrameCold, stateObjectsInFrameHot);
							stateObjectsInFrameHot.clear();
						}
						frame.setObjectsHot(new Int2ReferenceOpenHashMap<GameStateObject>(stateObjectsInFrameHot));
					}
					
					frame.setObjectsCold(stateObjectsInFrameCold);
					frame.getContext().finishBuilding();
					
					progressPercentCallback.accept(0.75 + ((((double) i) / numFrames) / PROGRESS_BAR_PHASES));
				}
				
				// parse singletons
				progressTextCallback.accept("Parsing singletons");
				var visitor = new VersionedObjectVisitor(interner);
				var singletons = singletonStates
						.stream()
						.map(state -> {
							var properties = new X2VersionedMap(0);
							visitor.setRootObject(0, properties);
							try {
								historyIndex.parseObject(state.fileEntry, visitor);
							} catch (IOException e) {
								// should never happen
								throw new UncheckedIOException(e);
							}
							return new HistorySingletonObject(
									state.fileEntry.getLength(), state.frame.getNumber(),
									state.fileEntry.getType(), properties, interner);
						})
						.sorted(Comparator
								.<HistorySingletonObject, UnrealName>comparing(s -> s.getType())
								.thenComparingInt(s -> s.getFirstFrame()))
						.toList();
				
				return new HistoryFile(
						history.CurrRandomSeed,
						history.NumArchivedFrames,
						Arrays.asList(parsedFrames),
						singletons,
						problemsDetected,
						contextTypes.stream().sorted().toList(),
						objectTypes.stream().sorted().toList(),
						interner);
			}
		} finally {
			Files.deleteIfExists(decompressedFile);
		}
	}
	
	private void readInParallel(String threadNamePrefix, FailableRunnable<Throwable> task) throws Exception {
		var errorRef = new AtomicReference<Throwable>();
		int numThreads = Runtime.getRuntime().availableProcessors() / 2; // TODO should be configurable
		var threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(
					() -> {
						try {
							task.run();
						} catch (Throwable t) {
							if (errorRef.compareAndSet(null, t)) {
								for (var thread : threads) {
									thread.interrupt();
								}
							} else {
								errorRef.get().addSuppressed(t);
							}
						}
					},
					threadNamePrefix + i);
		}
		for (var thread : threads) {
			thread.start(); // reminder: threads array must be fully populated before starting them
		}
		for (var thread : threads) {
			thread.join(); // wait for all threads to finish
		}
		var error = errorRef.get();
		if (error != null) {
			throw error instanceof Exception ex ? ex : new RuntimeException(error);
		}
	}
	
	private void readGameStateContexts(
			AtomicInteger nextArrayIndex, X2HistoryIndex historyIndex,
			X2HistoryIndexEntry[] contextArray, HistoryFrame[] frameArray, int frameOffset,
			PrimitiveInterner interner, List<HistoryFileProblem> problemsDetected,
			AtomicInteger objectsRemaining, Consumer<String> progressTextCallback,
			DoubleConsumer progressPercentCallback, int objectsTotal) throws IOException {
		var visitor = new VersionedObjectVisitor(interner);
		int arrayIndex;
		while (!Thread.interrupted() && (arrayIndex = nextArrayIndex.getAndIncrement()) < contextArray.length) {
			var contextEntry = contextArray[arrayIndex];
			var contextObject = new X2VersionedMap(0);
			visitor.setRootObject(0, contextObject);
			historyIndex.parseObject(contextEntry, visitor);
			contextObject.parseFinished();
			var frame = frameArray[arrayIndex];
			frame.setContext(new GameStateContext(
					contextEntry.getLength(), contextEntry.getType(), contextObject, frame, frameOffset,
					frameArray, interner, ScriptPreferences.CONTEXT_SUMMARY.getExecutable(), problemsDetected));
			int remaining = objectsRemaining.decrementAndGet();
			progressTextCallback.accept("Parsing contexts. " + remaining + " remaining.");
			progressPercentCallback.accept(0.5 + ((1 - (((double) remaining) / objectsTotal)) / PROGRESS_BAR_PHASES));
		}
	}
	
	private void readGameStateObjects(
			int frameOffset, X2HistoryIndex historyIndex, List<Set<GameStateObject>> objectsChangedInFrame,
			PrimitiveInterner interner, List<HistoryFileProblem> problemsDetected,
			BlockingQueue<List<GameStateObjectState>> queue, AtomicInteger objectsRemaining,
			Consumer<String> progressTextCallback, DoubleConsumer progressPercentCallback,
			int objectsTotal) throws IOException {
		var visitor = new VersionedObjectVisitor(interner);
		List<GameStateObjectState> states;
		while (!Thread.interrupted() && (states = queue.poll()) != null) {
			var parsedObject = new X2VersionedMap(states.getFirst().frame.getNumber());
			GameStateObject previousVersion = null;
			for (var state : states) {
				int frameNum = state.frame.getNumber();
				visitor.setRootObject(frameNum, parsedObject);
				historyIndex.parseObject(state.fileEntry, visitor);
				var parsedObjectFields = parsedObject.getValueAt(frameNum);
				if (parsedObjectFields.get(OBJECT_ID) == null &&
						(int) parsedObjectFields.get(PREV_FRAME_HIST_INDEX) > frameNum) {
					// object has no ID and previous frame index points to the future
					// this is a sign of https://github.com/rcd47/xcom-2-data/issues/2
					// note that in this situation, the previousVersionIndex above is -1
					// so we are not corrupting our tracking of any objects by doing parseObject()
					problemsDetected.add(new HistoryFileProblem(
							state.frame,
							null,
							null,
							String.format(
									PROBLEM_MODIFIED_OLD_STATE,
									state.fileEntry.getType().getOriginal(),
									parsedObjectFields.get(PREV_FRAME_HIST_INDEX))));
				} else {
					previousVersion = new GameStateObject(
							state.fileEntry.getLength(), previousVersion, parsedObject, state.fileEntry.getType(),
							state.frame, interner, ScriptPreferences.STATE_OBJECT_SUMMARY.getExecutable(), problemsDetected);
					objectsChangedInFrame.get(frameNum - frameOffset).add(previousVersion);
				}
			}
			parsedObject.parseFinished();
			int remaining = objectsRemaining.decrementAndGet();
			progressTextCallback.accept("Parsing objects. " + remaining + " remaining.");
			progressPercentCallback.accept(0.25 + ((1 - (((double) remaining) / objectsTotal)) / PROGRESS_BAR_PHASES));
		}
	}
	
	private static class GameStateObjectState {
		private final X2HistoryIndexEntry fileEntry;
		private final HistoryFrame frame;
		
		public GameStateObjectState(X2HistoryIndexEntry fileEntry, HistoryFrame frame) {
			this.fileEntry = fileEntry;
			this.frame = frame;
		}
	}
	
}
