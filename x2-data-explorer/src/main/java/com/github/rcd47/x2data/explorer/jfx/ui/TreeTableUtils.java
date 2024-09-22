package com.github.rcd47.x2data.explorer.jfx.ui;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class TreeTableUtils {
	
	public static void addDefaultExpandListener(BooleanProperty defaultExpand, TreeTableView<?> treeTable) {
		treeTable.rootProperty().addListener((_, _, root) -> {
			if (defaultExpand.get()) {
				recursiveSetExpanded(root, true);
			}
		});
	}
	
	public static void recursiveSetExpanded(TreeItem<?> item, boolean expanded) {
		recursiveSetExpanded(item, expanded, true);
	}
	
	private static void recursiveSetExpanded(TreeItem<?> item, boolean expanded, boolean root) {
		if (item == null) {
			return;
		}
		if (!root) {
			item.setExpanded(expanded);
		}
		for (var child : item.getChildren()) {
			recursiveSetExpanded(child, expanded, false);
		}
	}
	
}
