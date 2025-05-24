package com.github.rcd47.x2data.explorer.jfx.ui.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;

import com.github.rcd47.x2data.explorer.file.GameStateObject;
import com.github.rcd47.x2data.explorer.file.GameStateObjectField;
import com.github.rcd47.x2data.explorer.file.GameStateObjectFieldTreeNode;
import com.github.rcd47.x2data.explorer.file.HistoryFrame;
import com.github.rcd47.x2data.explorer.jfx.ui.MultiSelectMenu;
import com.github.rcd47.x2data.explorer.jfx.ui.StandardCellFactoryHelper;
import com.github.rcd47.x2data.explorer.jfx.ui.TreeTableUtils;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.explorer.prefs.ObjectPropertiesColumn;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ObjectPropertiesTable {
	
	private final TableView<HistoryFrame> framesTable;
	private final TableView<GameStateObject> objectsTable;
	private final TreeTableView<GameStateObjectFieldTreeNode> table;
	private final VBox vbox;
	
	public ObjectPropertiesTable(TableView<HistoryFrame> framesTable, TableView<GameStateObject> objectsTable) {
		this.framesTable = framesTable;
		this.objectsTable = objectsTable;
		
		// early initialization
		
		table = new TreeTableView<GameStateObjectFieldTreeNode>();
		
		// columns
		
		var columns = new EnumMap<ObjectPropertiesColumn, TreeTableColumn<GameStateObjectFieldTreeNode, ?>>(ObjectPropertiesColumn.class);
		
		var colName = new TreeTableColumn<GameStateObjectFieldTreeNode, GameStateObjectFieldTreeNode>(
				ObjectPropertiesColumn.NAME.getHeaderText());
		colName.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue()));
		colName.setCellFactory(_ -> new ObjectPropertyNameColumnCell());
		colName.setUserData(ObjectPropertiesColumn.NAME);
		columns.put(ObjectPropertiesColumn.NAME, colName);
		
		var colPrevValue = new TreeTableColumn<GameStateObjectFieldTreeNode, Object>(
				ObjectPropertiesColumn.PREV_VALUE.getHeaderText());
		colPrevValue.setCellValueFactory(f -> {
			var field = f.getValue().getValue().getPreviousValue();
			return field == null ? null : new ReadOnlyObjectWrapper<>(field.getValue());
		});
		colPrevValue.setUserData(ObjectPropertiesColumn.PREV_VALUE);
		StandardCellFactoryHelper.setFactoryForObjectValueColumn(colPrevValue);
		columns.put(ObjectPropertiesColumn.PREV_VALUE, colPrevValue);
		
		var colCurrentValue = new TreeTableColumn<GameStateObjectFieldTreeNode, Object>(
				ObjectPropertiesColumn.CURRENT_VALUE.getHeaderText());
		colCurrentValue.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue().getValue()));
		colCurrentValue.setUserData(ObjectPropertiesColumn.CURRENT_VALUE);
		StandardCellFactoryHelper.setFactoryForObjectValueColumn(colCurrentValue);
		columns.put(ObjectPropertiesColumn.CURRENT_VALUE, colCurrentValue);
		
		var colNextValue = new TreeTableColumn<GameStateObjectFieldTreeNode, Object>(
				ObjectPropertiesColumn.NEXT_VALUE.getHeaderText());
		colNextValue.setCellValueFactory(f -> {
			var field = f.getValue().getValue().getNextValue();
			return field == null ? null : new ReadOnlyObjectWrapper<>(field.getValue());
		});
		colNextValue.setUserData(ObjectPropertiesColumn.NEXT_VALUE);
		StandardCellFactoryHelper.setFactoryForObjectValueColumn(colNextValue);
		columns.put(ObjectPropertiesColumn.NEXT_VALUE, colNextValue);
		
		var colPrevFrame = new TreeTableColumn<GameStateObjectFieldTreeNode, GameStateObjectField>(
				ObjectPropertiesColumn.PREV_FRAME.getHeaderText());
		colPrevFrame.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue().getPreviousValue()));
		colPrevFrame.setCellFactory(_ -> new ObjectPropertyFrameLinkColumnCell());
		colPrevFrame.setUserData(ObjectPropertiesColumn.PREV_FRAME);
		columns.put(ObjectPropertiesColumn.PREV_FRAME, colPrevFrame);
		
		var colNextFrame = new TreeTableColumn<GameStateObjectFieldTreeNode, GameStateObjectField>(
				ObjectPropertiesColumn.NEXT_FRAME.getHeaderText());
		colNextFrame.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue().getNextValue()));
		colNextFrame.setCellFactory(_ -> new ObjectPropertyFrameLinkColumnCell());
		colNextFrame.setUserData(ObjectPropertiesColumn.NEXT_FRAME);
		columns.put(ObjectPropertiesColumn.NEXT_FRAME, colNextFrame);
		
		// toolbar
		
		var prefs = GeneralPreferences.getEffective();
		
		var colSelector = new MultiSelectMenu<>(
				"columns",
				columns.values(),
				c -> c.getText(),
				(c, b) -> {
					c.visibleProperty().bind(b);
					return prefs.getObjPropsColumns().stream().filter(p -> p.getColumn() == c.getUserData()).findAny().get().getVisible().get();
				});
		
		var expandAll = new Button("Expand All");
		expandAll.setOnAction(_ -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), true));
		
		var collapseAll = new Button("Collapse All");
		collapseAll.setOnAction(_ -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), false));
		
		var modifiedCheckbox = new CheckBox("Only show modified");
		modifiedCheckbox.setSelected(true);
		
		var toolbar = new ToolBar(colSelector, expandAll, collapseAll, modifiedCheckbox);
		
		// table
		
		// TODO auto-resizing doesn't work well in a lot of cases. need to find a better approach.
		//table.setSkin(new AutoResizingTreeTableViewSkin<>(table));
		table.setPlaceholder(new Label("Click an object to view its properties"));
		table.getColumns().addAll(prefs.getObjPropsColumns().stream().map(c -> columns.get(c.getColumn())).toList());
		table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setShowRoot(false);
		table.rootProperty().bind(Bindings.createObjectBinding(
				() -> {
					var object = objectsTable.getSelectionModel().getSelectedItem();
					return object == null ? null : object.getFieldsAsTreeNode(modifiedCheckbox.isSelected());
				},
				modifiedCheckbox.selectedProperty(),
				objectsTable.getSelectionModel().selectedItemProperty()));
		TreeTableUtils.addDefaultExpandListener(prefs.getHistoryObjPropsTreeExpanded(), table);
		
		// top-level layout
		
		vbox = new VBox(toolbar, table);
	}
	
	public Node getNode() {
		return vbox;
	}
	
	private class ObjectPropertyNameColumnCell extends TreeTableCell<GameStateObjectFieldTreeNode, GameStateObjectFieldTreeNode> {
		public ObjectPropertyNameColumnCell() {
			StandardCellFactoryHelper.configureCellForValueColumn(this);
		}
		
		@Override
		protected void updateItem(GameStateObjectFieldTreeNode item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item.getName().getOriginal());
				Color color = switch (item.getChangeType()) {
					case ADDED -> Color.LAWNGREEN;
					case CHANGED -> Color.DEEPSKYBLUE;
					case NONE -> Color.SILVER;
					case REMOVED -> Color.RED;
					default -> throw new IllegalArgumentException("bug - unknown change type");
				};
				setGraphic(new Rectangle(10, 10, color));
			}
		}
	}
	
	private class ObjectPropertyFrameLinkColumnCell extends TreeTableCell<GameStateObjectFieldTreeNode, GameStateObjectField> {
		@Override
		protected void updateItem(GameStateObjectField item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				var state = item.getLastChangedAt();
				var frame = state.getFrame();
				var link = new Hyperlink(Integer.toString(frame.getNumber()));
				link.setOnAction(_ -> {
					var path = new ArrayList<UnrealName>();
					var treeItem = getTableRow().getTreeItem();
					while (true) {
						path.add(treeItem.getValue().getName());
						treeItem = treeItem.getParent();
						if (treeItem.getValue() == null) {
							// reached root node
							break;
						}
					}
					Collections.reverse(path);
					
					framesTable.getSelectionModel().select(frame);
					framesTable.scrollTo(framesTable.getSelectionModel().getSelectedIndex());
					objectsTable.getSelectionModel().select(state);
					objectsTable.scrollTo(objectsTable.getSelectionModel().getSelectedIndex());
					
					var newTreeItem = table.getRoot();
					for (var element : path) {
						newTreeItem.setExpanded(true);
						newTreeItem = newTreeItem.getChildren().stream().filter(i -> i.getValue().getName().equals(element)).findAny().get();
						
					}
					table.getSelectionModel().select(newTreeItem);
					table.scrollTo(table.getSelectionModel().getSelectedIndex());
				});
				setGraphic(link);
			}
		}
	}
	
}
