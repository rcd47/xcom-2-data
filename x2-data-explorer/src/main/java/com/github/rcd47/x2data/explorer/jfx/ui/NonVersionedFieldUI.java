package com.github.rcd47.x2data.explorer.jfx.ui;

import java.util.Map.Entry;

import com.github.rcd47.x2data.explorer.file.NonVersionedField;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NonVersionedFieldUI {
	
	private final TreeTableView<Entry<UnrealName, NonVersionedField>> table;
	private final Node node;
	
	@SuppressWarnings("unchecked")
	public NonVersionedFieldUI(BooleanProperty defaultExpand, String placeholder) {
		var colPropName = new TreeTableColumn<Entry<UnrealName, NonVersionedField>, String>("Property");
		colPropName.setPrefWidth(Region.USE_COMPUTED_SIZE);
		colPropName.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getValue().getKey().getOriginal()));
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colPropName);
		var colPropValue = new TreeTableColumn<Entry<UnrealName, NonVersionedField>, Object>("Value");
		colPropValue.setPrefWidth(Region.USE_COMPUTED_SIZE);
		colPropValue.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue().getValue().getValue()));
		StandardCellFactoryHelper.setFactoryForObjectValueColumn(colPropValue);
		
		table = new TreeTableView<Entry<UnrealName, NonVersionedField>>();
		table.setPlaceholder(new Label(placeholder));
		table.getColumns().addAll(colPropName, colPropValue);
		table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setShowRoot(false);
		TreeTableUtils.addDefaultExpandListener(defaultExpand, table);
		VBox.setVgrow(table, Priority.ALWAYS);
		
		var expandAll = new Button("Expand All");
		expandAll.setOnAction(e -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), true));
		
		var collapseAll = new Button("Collapse All");
		collapseAll.setOnAction(e -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), false));
		
		var toolbar = new ToolBar(expandAll, collapseAll);
		
		node = new VBox(toolbar, table);
	}
	
	public Node getNode() {
		return node;
	}

	public ObjectProperty<TreeItem<Entry<UnrealName, NonVersionedField>>> getRootProperty() {
		return table.rootProperty();
	}
	
}
