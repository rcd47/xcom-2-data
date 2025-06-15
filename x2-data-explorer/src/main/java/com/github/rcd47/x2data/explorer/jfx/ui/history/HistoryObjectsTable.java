package com.github.rcd47.x2data.explorer.jfx.ui.history;

import java.util.EnumMap;

import com.github.rcd47.x2data.explorer.file.GameStateObject;
import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.HistoryFrame;
import com.github.rcd47.x2data.explorer.jfx.ui.AutoResizingTableViewSkin;
import com.github.rcd47.x2data.explorer.jfx.ui.ExpressionFilter;
import com.github.rcd47.x2data.explorer.jfx.ui.FilterPredicateCombiner;
import com.github.rcd47.x2data.explorer.jfx.ui.MultiSelectMenu;
import com.github.rcd47.x2data.explorer.jfx.ui.StandardCellFactoryHelper;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.explorer.prefs.HistoryObjectsColumn;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HistoryObjectsTable {
	
	private final TableView<HistoryFrame> framesTable;
	private final TableView<GameStateObject> objectsTable;
	private final VBox vbox;
	
	public HistoryObjectsTable(HistoryFile history, TableView<HistoryFrame> framesTable) {
		this.framesTable = framesTable;
		
		// columns
		
		var columns = new EnumMap<HistoryObjectsColumn, TableColumn<GameStateObject, ?>>(HistoryObjectsColumn.class);
		
		var colId = new TableColumn<GameStateObject, GameStateObject>(HistoryObjectsColumn.ID.getHeaderText());
		colId.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
		colId.setCellFactory(_ -> new ObjectIdColumnCell());
		colId.setUserData(HistoryObjectsColumn.ID);
		columns.put(HistoryObjectsColumn.ID, colId);
		
		var colType = new TableColumn<GameStateObject, String>(HistoryObjectsColumn.TYPE.getHeaderText());
		colType.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getType().getOriginal()));
		colType.setUserData(HistoryObjectsColumn.TYPE);
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colType);
		columns.put(HistoryObjectsColumn.TYPE, colType);
		
		var colSummary = new TableColumn<GameStateObject, String>(HistoryObjectsColumn.SUMMARY.getHeaderText());
		colSummary.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getSummary()));
		colSummary.setUserData(HistoryObjectsColumn.SUMMARY);
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colSummary);
		columns.put(HistoryObjectsColumn.SUMMARY, colSummary);
		
		var colPrev = new TableColumn<GameStateObject, GameStateObject>(HistoryObjectsColumn.PREV_VERSION.getHeaderText());
		colPrev.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getPreviousVersion()));
		colPrev.setCellFactory(_ -> new GameStateObjectLinkColumnCell());
		colPrev.setUserData(HistoryObjectsColumn.PREV_VERSION);
		columns.put(HistoryObjectsColumn.PREV_VERSION, colPrev);
		
		var colNext = new TableColumn<GameStateObject, GameStateObject>(HistoryObjectsColumn.NEXT_VERSION.getHeaderText());
		colNext.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getNextVersion()));
		colNext.setCellFactory(_ -> new GameStateObjectLinkColumnCell());
		colNext.setUserData(HistoryObjectsColumn.NEXT_VERSION);
		columns.put(HistoryObjectsColumn.NEXT_VERSION, colNext);
		
		// toolbar
		
		var filters = new FilterPredicateCombiner<GameStateObject>(3);
		
		var prefs = GeneralPreferences.getEffective();
		
		var types = history
				.getFrames()
				.stream()
				.flatMap(f -> f.getObjects().values().stream())
				.map(o -> o.getType())
				.distinct()
				.sorted()
				.toList();
		
		var colSelector = new MultiSelectMenu<>(
				"columns",
				columns.values(),
				c -> c.getText(),
				(c, b) -> {
					c.visibleProperty().bind(b);
					return prefs.getObjectsColumns().stream().filter(p -> p.getColumn() == c.getUserData()).findAny().get().getVisible().get();
				});
		
		var typeSelector = new MultiSelectMenu<>("object types", types, n -> n.getOriginal(), s -> {
			filters.setFilter(0, s.size() == types.size() ? null : o -> s.contains(o.getType()));
		});
		
		var modifiedCheckbox = new CheckBox("Only show modified");
		modifiedCheckbox.selectedProperty().addListener((_, _, newVal) -> {
			filters.setFilter(1, newVal ? o -> o.getFrame() == framesTable.getSelectionModel().getSelectedItem() : null);
		});
		modifiedCheckbox.setSelected(true);
		
		var expressionFilter = new ExpressionFilter<>(filters, 2, "o").getNode();
		
		var objectsToolbar = new ToolBar(colSelector, typeSelector, expressionFilter, modifiedCheckbox);
		
		// table
		
		objectsTable = new TableView<>();
		objectsTable.setSkin(new AutoResizingTableViewSkin<>(objectsTable));
		objectsTable.setPlaceholder(new Label("Click a frame to view its objects"));
		objectsTable.getColumns().addAll(prefs.getObjectsColumns().stream().map(c -> columns.get(c.getColumn())).toList());
		objectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		objectsTable.itemsProperty().bind(Bindings.createObjectBinding(
				() -> {
					var selectedFrame = framesTable.getSelectionModel().selectedItemProperty().get();
					return selectedFrame == null ? null : FXCollections.observableList(
							selectedFrame
							.getObjects()
							.values()
							.stream()
							.filter(filters.getProperty().get())
							.sorted((a, b) -> Integer.compare(a.getObjectId(), b.getObjectId()))
							.toList());
				},
				framesTable.getSelectionModel().selectedItemProperty(),
				filters.getProperty()));
		objectsTable.getSelectionModel().selectedItemProperty().addListener((_, oldVal, newVal) -> {
			if (newVal == null) {
				objectsTable
						.getItems()
						.stream()
						.filter(o -> o.getObjectId() == oldVal.getObjectId())
						.findAny()
						.ifPresent(o -> Platform.runLater(() -> objectsTable.getSelectionModel().select(o)));
			}
		});
		VBox.setVgrow(objectsTable, Priority.ALWAYS);
		
		// top-level layout
		
		vbox = new VBox(objectsToolbar, objectsTable);
	}
	
	public Node getNode() {
		return vbox;
	}
	
	public TableView<GameStateObject> getObjectsTable() {
		return objectsTable;
	}

	private class ObjectIdColumnCell extends TableCell<GameStateObject, GameStateObject> {
		public ObjectIdColumnCell() {
			graphicProperty().bind(Bindings.createObjectBinding(
					() -> {
						var selectedFrame = framesTable.getSelectionModel().getSelectedItem();
						var gameStateObject = getItem();
						if (selectedFrame == null || gameStateObject == null) {
							return null;
						}
						
						// note that we must check removed first
						// it is possible for an object to be added and removed in the same state
						Color color;
						if (gameStateObject.getFrame() == selectedFrame) { // deliberate identity comparison
							if (gameStateObject.isRemoved()) {
								color = Color.RED;
							} else if (gameStateObject.getPreviousVersion() == null) {
								color = Color.LAWNGREEN;
							} else {
								color = Color.DEEPSKYBLUE;
							}
						} else {
							color = Color.SILVER;
						}
						return new Rectangle(10, 10, color);
					},
					itemProperty(),
					framesTable.getSelectionModel().selectedItemProperty()));
		}
		
		@Override
		protected void updateItem(GameStateObject item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
			} else {
				setText(Integer.toString(item.getObjectId()));
			}
		}
	}
	
	private class GameStateObjectLinkColumnCell extends TableCell<GameStateObject, GameStateObject> {
		@Override
		protected void updateItem(GameStateObject item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				var link = new Hyperlink(Integer.toString(item.getFrame().getNumber()));
				link.setOnAction(_ -> {
					framesTable.getSelectionModel().select(item.getFrame());
					framesTable.scrollTo(framesTable.getSelectionModel().getSelectedIndex());
					objectsTable.getSelectionModel().select(item);
					objectsTable.scrollTo(objectsTable.getSelectionModel().getSelectedIndex());
				});
				setGraphic(link);
			}
		}
	}
	
}
