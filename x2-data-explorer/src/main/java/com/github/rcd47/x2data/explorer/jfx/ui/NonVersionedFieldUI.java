package com.github.rcd47.x2data.explorer.jfx.ui;

import com.github.rcd47.x2data.explorer.file.data.X2VersionedDatumTreeItem;

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
	
	private final TreeTableView<X2VersionedDatumTreeItem> table;
	private final Node node;
	
	@SuppressWarnings("unchecked")
	public NonVersionedFieldUI(BooleanProperty defaultExpand, String placeholder) {
		var colPropName = new TreeTableColumn<X2VersionedDatumTreeItem, String>("Property");
		colPropName.setPrefWidth(Region.USE_COMPUTED_SIZE);
		colPropName.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getValue().getName().getOriginal()));
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colPropName);
		var colPropValue = new TreeTableColumn<X2VersionedDatumTreeItem, Object>("Value");
		colPropValue.setPrefWidth(Region.USE_COMPUTED_SIZE);
		colPropValue.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue().getValue()));
		StandardCellFactoryHelper.setFactoryForObjectValueColumn(colPropValue);
		
		table = new TreeTableView<>();
		table.setPlaceholder(new Label(placeholder));
		table.getColumns().addAll(colPropName, colPropValue);
		table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setShowRoot(false);
		TreeTableUtils.addDefaultExpandListener(defaultExpand, table);
		VBox.setVgrow(table, Priority.ALWAYS);
		
		var expandAll = new Button("Expand All");
		expandAll.setOnAction(_ -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), true));
		
		var collapseAll = new Button("Collapse All");
		collapseAll.setOnAction(_ -> TreeTableUtils.recursiveSetExpanded(table.getRoot(), false));
		
		var toolbar = new ToolBar(expandAll, collapseAll);
		
		node = new VBox(toolbar, table);
	}
	
	public Node getNode() {
		return node;
	}

	public ObjectProperty<TreeItem<X2VersionedDatumTreeItem>> getRootProperty() {
		return table.rootProperty();
	}
	
}
