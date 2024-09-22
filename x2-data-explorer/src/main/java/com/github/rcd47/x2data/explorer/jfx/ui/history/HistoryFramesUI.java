package com.github.rcd47.x2data.explorer.jfx.ui.history;

import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.NonVersionedField;
import com.github.rcd47.x2data.explorer.jfx.ui.NonVersionedFieldUI;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class HistoryFramesUI {
	
	private final SplitPane mainSplit;
	
	public HistoryFramesUI(HistoryFile history) {
		var framesTableUI = new HistoryFramesTable(history);
		var framesTable = framesTableUI.getTable();
		
		var objectsTableUI = new HistoryObjectsTable(history, framesTable);
		
		var objectPropsTableUI = new ObjectPropertiesTable(framesTable, objectsTableUI.getObjectsTable());
		
		var contextPropsUI = new NonVersionedFieldUI(
				GeneralPreferences.getEffective().getHistoryContextPropsTreeExpanded(),
				"Click a frame to view its context's properties");
		contextPropsUI.getRootProperty().bind(
				framesTable.getSelectionModel().selectedItemProperty().map(f -> NonVersionedField.convertToTreeItems(f.getContext().getFields())));
		
		var framesSplit = new SplitPane(framesTableUI.getNode(), contextPropsUI.getNode());
		framesSplit.setOrientation(Orientation.HORIZONTAL);
		framesSplit.setDividerPosition(0, 0.75);
		
		var objectsSplit = new SplitPane(objectsTableUI.getNode(), objectPropsTableUI.getNode());
		objectsSplit.setOrientation(Orientation.HORIZONTAL);
		
		mainSplit = new SplitPane(framesSplit, objectsSplit);
		mainSplit.setOrientation(Orientation.VERTICAL);
	}
	
	public Node getNode() {
		return mainSplit;
	}
	
}
