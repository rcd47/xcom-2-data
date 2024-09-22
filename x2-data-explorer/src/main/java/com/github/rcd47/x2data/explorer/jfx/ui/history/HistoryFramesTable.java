package com.github.rcd47.x2data.explorer.jfx.ui.history;

import java.util.Arrays;
import java.util.EnumMap;

import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.HistoryFrame;
import com.github.rcd47.x2data.explorer.jfx.ui.ExpressionFilter;
import com.github.rcd47.x2data.explorer.jfx.ui.FilterPredicateCombiner;
import com.github.rcd47.x2data.explorer.jfx.ui.MultiSelectMenu;
import com.github.rcd47.x2data.explorer.jfx.ui.StandardCellFactoryHelper;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.explorer.prefs.HistoryFramesColumn;
import com.github.rcd47.x2data.lib.unreal.mappings.base.EInterruptionStatus;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryFramesTable {
	
	private static final String[] INTERRUPT_STATUSES =
			Arrays.stream(EInterruptionStatus.values()).map(s -> s.name().substring(20)).toArray(String[]::new);
	
	private final TableView<HistoryFrame> table;
	private final VBox vbox;
	
	public HistoryFramesTable(HistoryFile history) {
		// columns
		
		var columns = new EnumMap<HistoryFramesColumn, TableColumn<HistoryFrame, ?>>(HistoryFramesColumn.class);
		
		var colNum = new TableColumn<HistoryFrame, Integer>(HistoryFramesColumn.FRAME_NUMBER.getHeaderText());
		colNum.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getNumber()).asObject());
		colNum.setUserData(HistoryFramesColumn.FRAME_NUMBER);
		columns.put(HistoryFramesColumn.FRAME_NUMBER, colNum);
		
		var colTime = new TableColumn<HistoryFrame, String>(HistoryFramesColumn.TIMESTAMP.getHeaderText());
		colTime.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getTimestamp()));
		colTime.setUserData(HistoryFramesColumn.TIMESTAMP);
		columns.put(HistoryFramesColumn.TIMESTAMP, colTime);
		
		var colCtxType = new TableColumn<HistoryFrame, String>(HistoryFramesColumn.CONTEXT_TYPE.getHeaderText());
		colCtxType.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getContext().getType().getOriginal()));
		colCtxType.setUserData(HistoryFramesColumn.CONTEXT_TYPE);
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colCtxType);
		columns.put(HistoryFramesColumn.CONTEXT_TYPE, colCtxType);
		
		var colCtxSummary = new TableColumn<HistoryFrame, String>(HistoryFramesColumn.CONTEXT_SUMMARY.getHeaderText());
		colCtxSummary.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getContext().getSummary()));
		colCtxSummary.setUserData(HistoryFramesColumn.CONTEXT_SUMMARY);
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colCtxSummary);
		columns.put(HistoryFramesColumn.CONTEXT_SUMMARY, colCtxSummary);
		
		var colInterruptStatus = new TableColumn<HistoryFrame, String>(HistoryFramesColumn.INTERRUPT_STATUS.getHeaderText());
		colInterruptStatus.setCellValueFactory(f ->
				new ReadOnlyStringWrapper(INTERRUPT_STATUSES[f.getValue().getContext().getInterruptionStatus().ordinal()]));
		colInterruptStatus.setUserData(HistoryFramesColumn.INTERRUPT_STATUS);
		columns.put(HistoryFramesColumn.INTERRUPT_STATUS, colInterruptStatus);
		
		var colResumedFrom = new TableColumn<HistoryFrame, HistoryFrame>(HistoryFramesColumn.RESUMED_FROM.getHeaderText());
		colResumedFrom.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getContext().getResumedFrom()));
		colResumedFrom.setCellFactory(c -> new HistoryFrameLinkColumnCell<>());
		colResumedFrom.setUserData(HistoryFramesColumn.RESUMED_FROM);
		columns.put(HistoryFramesColumn.RESUMED_FROM, colResumedFrom);
		
		var colResumedBy = new TableColumn<HistoryFrame, HistoryFrame>(HistoryFramesColumn.RESUMED_BY.getHeaderText());
		colResumedBy.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getContext().getResumedBy()));
		colResumedBy.setCellFactory(c -> new HistoryFrameLinkColumnCell<>());
		colResumedBy.setUserData(HistoryFramesColumn.RESUMED_BY);
		columns.put(HistoryFramesColumn.RESUMED_BY, colResumedBy);
		
		var colInterruptedByThis = new TableColumn<HistoryFrame, HistoryFrame>(HistoryFramesColumn.INTERRUPTED.getHeaderText());
		colInterruptedByThis.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getContext().getInterruptedByThis()));
		colInterruptedByThis.setCellFactory(c -> new HistoryFrameLinkColumnCell<>());
		colInterruptedByThis.setUserData(HistoryFramesColumn.INTERRUPTED);
		columns.put(HistoryFramesColumn.INTERRUPTED, colInterruptedByThis);
		
		// toolbar
		
		var filters = new FilterPredicateCombiner<HistoryFrame>(3);
		
		var prefs = GeneralPreferences.getEffective();
		
		var colSelector = new MultiSelectMenu<>(
				"columns",
				columns.values(),
				c -> c.getText(),
				(c, b) -> {
					c.visibleProperty().bind(b);
					return prefs.getFramesColumns().stream().filter(p -> p.getColumn() == c.getUserData()).findAny().get().getVisible().get();
				});
		
		var toolbar = new ToolBar(colSelector);
		
		var contextTypes = history.getFrames().stream().map(f -> f.getContext().getType()).distinct().sorted().toList();
		
		var contextTypeSelector = new MultiSelectMenu<>("context types", contextTypes, n -> n.getOriginal(), s -> {
			filters.setFilter(0, s.size() == contextTypes.size() ? null : f -> s.contains(f.getContext().getType()));
		});
		
		var interruptStatesSelector = new MultiSelectMenu<>(
				"interrupt statuses", Arrays.asList(EInterruptionStatus.values()), n -> INTERRUPT_STATUSES[n.ordinal()],
				s -> {
					filters.setFilter(1, s.size() == INTERRUPT_STATUSES.length ? null : f -> s.contains(f.getContext().getInterruptionStatus()));
				});
		
		var expressionFilter = new ExpressionFilter<>(filters, 2, "f").getNode();
		
		toolbar.getItems().addAll(contextTypeSelector, interruptStatesSelector, expressionFilter);
		
		// table
		
		table = new TableView<>();
		table.getColumns().addAll(prefs.getFramesColumns().stream().map(c -> columns.get(c.getColumn())).toList());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.itemsProperty().bind(
				filters.getProperty().map(p -> FXCollections.observableList(history.getFrames().stream().filter(p).toList())));
		
		// top-level layout
		
		vbox = new VBox(toolbar, table);
	}
	
	public Region getNode() {
		return vbox;
	}
	
	public TableView<HistoryFrame> getTable() {
		return table;
	}

	private class HistoryFrameLinkColumnCell<S> extends TableCell<S, HistoryFrame> {
		@Override
		protected void updateItem(HistoryFrame item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				var link = new Hyperlink(Integer.toString(item.getNumber()));
				link.setOnAction(e -> {
					var selectionModel = table.getSelectionModel();
					selectionModel.select(item);
					table.scrollTo(selectionModel.getSelectedIndex());
				});
				setGraphic(link);
			}
		}
	}
	
}
