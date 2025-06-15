package com.github.rcd47.x2data.explorer.jfx.ui.history;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import com.github.rcd47.x2data.explorer.file.GameStateContext;
import com.github.rcd47.x2data.explorer.file.GameStateObject;
import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.HistorySingletonObject;
import com.github.rcd47.x2data.explorer.file.ISizedObject;
import com.github.rcd47.x2data.explorer.file.NonVersionedField;
import com.github.rcd47.x2data.explorer.jfx.ui.NonVersionedFieldUI;
import com.github.rcd47.x2data.explorer.jfx.ui.ProgressUtils;
import com.github.rcd47.x2data.explorer.jfx.ui.StandardCellFactoryHelper;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryBloatAnalysisUI {
	
	private static final int LARGEST_LIMIT = 500;
	
	public HistoryBloatAnalysisUI(HistoryFile history, Tab parent) {
		var task = new Task<Node>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Node call() throws Exception {
				// analysis
				
				updateProgress(0, 1);
				var perObjectStatsMap = new HashMap<Integer, GameStateObjectStats>();
				var perContextClassStatsMap = new HashMap<UnrealName, IntSummaryStatistics>();
				var largestDeltaObjects = new LargestList<GameStateObject>();
				var largestFullObjects = new LargestList<GameStateObject>();
				var largestContexts = new LargestList<GameStateContext>();
				var frames = history.getFrames();
				var numFrames = frames.size();
				for (int i = 0; i < numFrames; i++) {
					var frame = frames.get(i);
					updateMessage("Analayzing frame " + frame.getNumber());
					var context = frame.getContext();
					perContextClassStatsMap.computeIfAbsent(context.getType(), _ -> new IntSummaryStatistics()).accept(context.getSizeInFile());
					largestContexts.add(context);
					for (var gso : frame.getObjects().values()) {
						if (gso.getFrame().equals(frame)) {
							perObjectStatsMap.computeIfAbsent(gso.getObjectId(), _ -> new GameStateObjectStats()).add(gso);
							(gso.getPreviousVersion() == null ? largestFullObjects : largestDeltaObjects).add(gso);
						}
					}
					updateProgress(((double) i + 1) / numFrames, 1);
				}
				var perObjectStats = FXCollections.observableArrayList(perObjectStatsMap.values());
				var perContextClassStats = FXCollections.observableArrayList(perContextClassStatsMap.entrySet());
				
				var perObjectClassStatsMap = new HashMap<UnrealName, GameStateClassStats>();
				for (var objStats : perObjectStats) {
					perObjectClassStatsMap.computeIfAbsent(objStats.type, GameStateClassStats::new).add(objStats);
				}
				var perObjectClassStats = FXCollections.observableArrayList(perObjectClassStatsMap.values());
				
				// object class stats table
				
				var colClassType = new TableColumn<GameStateClassStats, String>("Class");
				colClassType.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().type.getOriginal()));
				
				var colClassObjCount = new TableColumn<GameStateClassStats, Integer>("# Objects");
				colClassObjCount.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().objectCount).asObject());
				formatNumericColumn(colClassObjCount, null);
				
				var colClassTotalBytes = new TableColumn<GameStateClassStats, Integer>("Total Bytes");
				colClassTotalBytes.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().totalBytes).asObject());
				formatNumericColumn(colClassTotalBytes, null);
				
				var colClassDeltaBytesAvg = new TableColumn<GameStateClassStats, Double>("Avg Bytes/Delta");
				colClassDeltaBytesAvg.setCellValueFactory(t -> new ReadOnlyDoubleWrapper(t.getValue().deltaByteStats.getAverage()).asObject());
				formatNumericColumn(colClassDeltaBytesAvg, null);
				
				var colClassDeltaBytesMin = new TableColumn<GameStateClassStats, Integer>("Min Bytes/Delta");
				colClassDeltaBytesMin.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().deltaByteStats.getMin()).asObject());
				formatNumericColumn(colClassDeltaBytesMin, Integer.MAX_VALUE);
				
				var colClassDeltaBytesMax = new TableColumn<GameStateClassStats, Integer>("Max Bytes/Delta");
				colClassDeltaBytesMax.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().deltaByteStats.getMax()).asObject());
				formatNumericColumn(colClassDeltaBytesMax, Integer.MIN_VALUE);
				
				var colClassDeltaCount = new TableColumn<GameStateClassStats, Long>("Total Deltas");
				colClassDeltaCount.setCellValueFactory(t -> new ReadOnlyLongWrapper(t.getValue().deltaCountStats.getSum()).asObject());
				formatNumericColumn(colClassDeltaCount, null);
				
				var colClassDeltaAvg = new TableColumn<GameStateClassStats, Double>("Avg Deltas");
				colClassDeltaAvg.setCellValueFactory(t -> new ReadOnlyDoubleWrapper(t.getValue().deltaCountStats.getAverage()).asObject());
				formatNumericColumn(colClassDeltaAvg, null);
				
				var colClassDeltaMin = new TableColumn<GameStateClassStats, Integer>("Min Deltas");
				colClassDeltaMin.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().deltaCountStats.getMin()).asObject());
				formatNumericColumn(colClassDeltaMin, null);
				
				var colClassDeltaMax = new TableColumn<GameStateClassStats, Integer>("Max Deltas");
				colClassDeltaMax.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().deltaCountStats.getMax()).asObject());
				formatNumericColumn(colClassDeltaMax, null);
				
				var classStatsTable = new TableView<>(perObjectClassStats);
				classStatsTable.getColumns().addAll(
						colClassType, colClassObjCount, colClassTotalBytes, colClassDeltaBytesAvg, colClassDeltaBytesMin,
						colClassDeltaBytesMax, colClassDeltaCount, colClassDeltaAvg, colClassDeltaMin, colClassDeltaMax);
				
				// context class stats table
				
				var colContextType = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, String>("Class");
				colContextType.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getKey().getOriginal()));
				
				var colContextCount = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, Long>("# Frames");
				colContextCount.setCellValueFactory(t -> new ReadOnlyLongWrapper(t.getValue().getValue().getCount()).asObject());
				formatNumericColumn(colContextCount, null);
				
				var colContextBytesTotal = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, Long>("Total Bytes");
				colContextBytesTotal.setCellValueFactory(t -> new ReadOnlyLongWrapper(t.getValue().getValue().getSum()).asObject());
				formatNumericColumn(colContextBytesTotal, null);
				
				var colContextBytesAvg = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, Double>("Avg Bytes");
				colContextBytesAvg.setCellValueFactory(t -> new ReadOnlyDoubleWrapper(t.getValue().getValue().getAverage()).asObject());
				formatNumericColumn(colContextBytesAvg, null);
				
				var colContextBytesMin = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, Integer>("Min Bytes");
				colContextBytesMin.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getValue().getMin()).asObject());
				formatNumericColumn(colContextBytesMin, null);
				
				var colContextBytesMax = new TableColumn<Entry<UnrealName, IntSummaryStatistics>, Integer>("Max Bytes");
				colContextBytesMax.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getValue().getMax()).asObject());
				formatNumericColumn(colContextBytesMax, null);
				
				var contextStatsTable = new TableView<>(perContextClassStats);
				contextStatsTable.getColumns().addAll(
						colContextType, colContextCount, colContextBytesTotal, colContextBytesAvg, colContextBytesMin, colContextBytesMax);
				
				// largest delta objects
				
				var colDeltaObjId = new TableColumn<GameStateObject, Integer>("Object ID");
				colDeltaObjId.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getObjectId()).asObject());
				
				var colDeltaObjType = new TableColumn<GameStateObject, String>("Object Class");
				colDeltaObjType.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getType().getOriginal()));
				
				var colDeltaSize = new TableColumn<GameStateObject, Integer>("Delta Size");
				colDeltaSize.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getSizeInFile()).asObject());
				formatNumericColumn(colDeltaSize, null);
				
				var colDeltaObjSummary = new TableColumn<GameStateObject, String>("Object Summary");
				colDeltaObjSummary.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getSummary()));
				
				var colDeltaFrameNum = new TableColumn<GameStateObject, Integer>("Frame #");
				colDeltaFrameNum.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getFrame().getNumber()).asObject());
				
				var colDeltaCtxSummary = new TableColumn<GameStateObject, String>("Context Summary");
				colDeltaCtxSummary.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getFrame().getContext().getSummary()));
				
				var deltaObjectsTable = new TableView<>(largestDeltaObjects.toList());
				deltaObjectsTable.getColumns().addAll(
						colDeltaObjId, colDeltaObjType, colDeltaSize, colDeltaObjSummary, colDeltaFrameNum, colDeltaCtxSummary);
				
				var deltaSplitPane = new SplitPane(deltaObjectsTable, new ObjectPropertiesTable(null, deltaObjectsTable).getNode());
				deltaSplitPane.setOrientation(Orientation.HORIZONTAL);
				
				// largest full objects
				
				var colFullObjId = new TableColumn<GameStateObject, Integer>("Object ID");
				colFullObjId.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getObjectId()).asObject());
				
				var colFullObjType = new TableColumn<GameStateObject, String>("Object Class");
				colFullObjType.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getType().getOriginal()));
				
				var colFullSize = new TableColumn<GameStateObject, Integer>("Object Size");
				colFullSize.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getSizeInFile()).asObject());
				formatNumericColumn(colFullSize, null);
				
				var colFullObjSummary = new TableColumn<GameStateObject, String>("Object Summary");
				colFullObjSummary.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getSummary()));
				
				var colFullFrameNum = new TableColumn<GameStateObject, Integer>("Frame #");
				colFullFrameNum.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getFrame().getNumber()).asObject());
				
				var colFullCtxSummary = new TableColumn<GameStateObject, String>("Context Summary");
				colFullCtxSummary.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getFrame().getContext().getSummary()));
				
				var fullObjectsTable = new TableView<>(largestFullObjects.toList());
				fullObjectsTable.getColumns().addAll(
						colFullObjId, colFullObjType, colFullSize, colFullObjSummary, colFullFrameNum, colFullCtxSummary);
				
				var fullSplitPane = new SplitPane(fullObjectsTable, new ObjectPropertiesTable(null, fullObjectsTable).getNode());
				fullSplitPane.setOrientation(Orientation.HORIZONTAL);
				
				// largest contexts
				
				var colCtxFrameNum = new TableColumn<GameStateContext, Integer>("Frame #");
				colCtxFrameNum.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getFrame().getNumber()).asObject());
				
				var colCtxType = new TableColumn<GameStateContext, String>("Context Class");
				colCtxType.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getType().getOriginal()));
				
				var colCtxSize = new TableColumn<GameStateContext, Integer>("Context Size");
				colCtxSize.setCellValueFactory(t -> new ReadOnlyIntegerWrapper(t.getValue().getSizeInFile()).asObject());
				formatNumericColumn(colCtxSize, null);
				
				var colCtxSummary = new TableColumn<GameStateContext, String>("Context Summary");
				colCtxSummary.setCellValueFactory(t -> new ReadOnlyStringWrapper(t.getValue().getSummary()));
				
				var ctxTable = new TableView<>(largestContexts.toList());
				ctxTable.getColumns().addAll(colCtxFrameNum, colCtxType, colCtxSize, colCtxSummary);
				
				var ctxProperties = new NonVersionedFieldUI(
						GeneralPreferences.getEffective().getHistoryContextPropsTreeExpanded(),
						"Click a context to view its properties");
				ctxProperties.getRootProperty().bind(
						ctxTable.getSelectionModel().selectedItemProperty().map(f -> NonVersionedField.convertToTreeItems(f.getFields())));
				
				var ctxSplitPane = new SplitPane(ctxTable, ctxProperties.getNode());
				ctxSplitPane.setOrientation(Orientation.HORIZONTAL);
				
				// singletons
				
				var colSingletonId = new TableColumn<HistorySingletonObject, Integer>("Singleton ID");
				colSingletonId.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getObjectId()).asObject());
				var colSingletonType = new TableColumn<HistorySingletonObject, String>("Singleton Type");
				colSingletonType.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getType().getOriginal()));
				StandardCellFactoryHelper.setFactoryForStringValueColumn(colSingletonType);
				var colSingletonFirstFrame = new TableColumn<HistorySingletonObject, Integer>("First Frame");
				colSingletonFirstFrame.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getFirstFrame()).asObject());
				var colSingletonSize = new TableColumn<HistorySingletonObject, Integer>("Size");
				colSingletonSize.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getSizeInFile()).asObject());
				formatNumericColumn(colSingletonSize, null);
				
				var singletonsTable = new TableView<>(FXCollections.observableList(history.getSingletons()));
				singletonsTable.getColumns().addAll(colSingletonId, colSingletonType, colSingletonFirstFrame, colSingletonSize);
				singletonsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
				
				var singletonPropsUI = new NonVersionedFieldUI(
						GeneralPreferences.getEffective().getHistorySingletonPropsTreeExpanded(),
						"Click a singleton state to view its properties");
				singletonPropsUI.getRootProperty().bind(
						singletonsTable.getSelectionModel().selectedItemProperty().map(f -> NonVersionedField.convertToTreeItems(f.getFields())));
				
				var singletonSplitPane = new SplitPane(singletonsTable, singletonPropsUI.getNode());
				singletonSplitPane.setOrientation(Orientation.HORIZONTAL);
				
				// tab pane
				
				var tabObjClassStats = new Tab("Object Class Stats", classStatsTable);
				tabObjClassStats.setClosable(false);
				
				var tabContextClassStats = new Tab("Context Class Stats", contextStatsTable);
				tabContextClassStats.setClosable(false);
				
				var tabLargestDeltaObjects = new Tab("Largest Delta Objects", deltaSplitPane);
				tabLargestDeltaObjects.setClosable(false);
				
				var tabLargestFullObjects = new Tab("Largest Full Objects", fullSplitPane);
				tabLargestFullObjects.setClosable(false);
				
				var tabLargestContexts = new Tab("Largest Contexts", ctxSplitPane);
				tabLargestContexts.setClosable(false);
				
				var tabSingletons = new Tab("Singletons", singletonSplitPane);
				tabSingletons.setClosable(false);
				
				return new TabPane(tabObjClassStats, tabContextClassStats, tabLargestDeltaObjects, tabLargestFullObjects, tabLargestContexts, tabSingletons);
			}

			@Override
			protected void succeeded() {
				parent.setContent(getValue());
			}

			@Override
			protected void failed() {
				parent.setContent(ProgressUtils.createTaskFailureUi(getException()));
			}
		};
		
		parent.setContent(ProgressUtils.createProgressUi(task));
		parent.selectedProperty().addListener((_, _, selected) -> {
			if (selected && !task.isDone() && !task.isRunning()) {
				new Thread(task, "BloatAnalysis").start();
			}
		});
	}
	
	private static <S, T extends Number> void formatNumericColumn(TableColumn<S, T> col, T nullValue) {
		var numFmt = NumberFormat.getInstance();
		
		col.setCellFactory(_ -> {
			var cell = new TableCell<S, T>();
			cell.textProperty().bind(cell.itemProperty().map(v -> v.equals(nullValue) ? "-" : numFmt.format(v)));
			return cell;
		});
	}
	
	private static class GameStateClassStats {
		UnrealName type;
		int objectCount;
		int totalBytes;
		IntSummaryStatistics deltaByteStats;
		IntSummaryStatistics deltaCountStats;
		
		GameStateClassStats(UnrealName type) {
			this.type = type;
			deltaByteStats = new IntSummaryStatistics();
			deltaCountStats = new IntSummaryStatistics();
		}

		void add(GameStateObjectStats objStats) {
			objectCount++;
			totalBytes += objStats.totalBytes;
			deltaByteStats.combine(objStats.deltaByteStats);
			deltaCountStats.accept(objStats.deltaCount);
		}
	}
	
	private static class GameStateObjectStats {
		int id;
		UnrealName type;
		IntSummaryStatistics deltaByteStats;
		int totalBytes;
		int deltaCount;
		
		void add(GameStateObject gso) {
			int bytes = gso.getSizeInFile();
			if (id == 0) {
				id = gso.getObjectId();
				type = gso.getType();
				deltaByteStats = new IntSummaryStatistics();
				totalBytes = bytes;
			} else {
				deltaByteStats.accept(bytes);
				totalBytes += bytes;
				deltaCount++;
			}
		}
	}
	
	private static class LargestList<T extends ISizedObject> {
		final Queue<T> queue;
		int queueSize;
		
		LargestList() {
			queue = new PriorityQueue<>(LARGEST_LIMIT, Comparator.comparingInt(ISizedObject::getSizeInFile));
		}
		
		void add(T obj) {
			if (queueSize < LARGEST_LIMIT) {
				queueSize++;
				queue.add(obj);
			} else if (obj.getSizeInFile() > queue.peek().getSizeInFile()) {
				queue.poll();
				queue.add(obj);
			} // else obj size is <= the smallest in queue, so nothing to do
		}
		
		ObservableList<T> toList() {
			var list = FXCollections.<T>observableArrayList();
			T obj;
			while ((obj = queue.poll()) != null) { // PriorityQueue's iterator() is unordered
				list.addFirst(obj);
			}
			return list;
		}
	}
	
}
