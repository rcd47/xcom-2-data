package com.github.rcd47.x2data.explorer.jfx.ui.history;

import java.util.ArrayList;

import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.HistorySingletonObject;
import com.github.rcd47.x2data.explorer.jfx.ui.NonVersionedFieldUI;
import com.github.rcd47.x2data.explorer.jfx.ui.StandardCellFactoryHelper;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.lib.savegame.X2DlcNamePair;
import com.github.rcd47.x2data.lib.savegame.X2GameVersion;
import com.github.rcd47.x2data.lib.savegame.X2SaveGameHeader;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryGeneralUI {
	
	private final SplitPane mainSplit;
	
	@SuppressWarnings("unchecked")
	public HistoryGeneralUI(X2SaveGameHeader saveHeader, HistoryFile historyFile) {
		var headers = new ArrayList<HeaderPair>();
		
		if (saveHeader != null) {
			headers.add(new HeaderPair("Game version", saveHeader.gameVersion.name()));
			headers.add(new HeaderPair("Campaign #", Integer.toString(saveHeader.campaignNumber)));
			headers.add(new HeaderPair("Save slot", Integer.toString(saveHeader.saveSlot)));
			headers.add(new HeaderPair("Description", saveHeader.description));
			headers.add(new HeaderPair("Creation time", saveHeader.creationTime.toString()));
			headers.add(new HeaderPair("Map command", saveHeader.mapCommand));
			headers.add(new HeaderPair("Is tactical save", Boolean.toString(saveHeader.tacticalSave)));
			headers.add(new HeaderPair("Is ironman enabled", Boolean.toString(saveHeader.ironmanEnabled)));
			headers.add(new HeaderPair("Is auto save", Boolean.toString(saveHeader.autoSave)));
			headers.add(new HeaderPair("Language", saveHeader.language));
			headers.add(new HeaderPair("Campaign start time", saveHeader.campaignStartTime.toString()));
			headers.add(new HeaderPair("Map image", saveHeader.mapImage));
			headers.add(new HeaderPair("Name", saveHeader.name));
			if (saveHeader.gameVersion != X2GameVersion.XCOM2) {
				headers.add(new HeaderPair("Mission #", Integer.toString(saveHeader.missionNumber)));
				headers.add(new HeaderPair("Campaign month", Integer.toString(saveHeader.campaignMonth)));
				headers.add(new HeaderPair("Tactical turn #", Integer.toString(saveHeader.tacticalTurn)));
				headers.add(new HeaderPair("Tactical action #", Integer.toString(saveHeader.tacticalAction)));
				if (saveHeader.gameVersion == X2GameVersion.XCOM2_WOTC_TLP) {
					headers.add(new HeaderPair("Mission type", saveHeader.missionType));
				}
			}
		}
		
		headers.add(new HeaderPair("Current random seed", Integer.toString(historyFile.getRandomSeed())));
		headers.add(new HeaderPair("# of archived frames", Integer.toString(historyFile.getNumArchivedFrames())));
		
		// headers table
		
		var colHeaderField = new TableColumn<HeaderPair, String>("Header Field");
		colHeaderField.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().key));
		var colHeaderValue = new TableColumn<HeaderPair, String>("Header Value");
		colHeaderValue.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().value));
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colHeaderValue);
		
		var headersTable = new TableView<>(FXCollections.observableList(headers));
		headersTable.getColumns().addAll(colHeaderField, colHeaderValue);
		headersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		
		// singleton states table
		
		var colSingletonId = new TableColumn<HistorySingletonObject, Integer>("Singleton ID");
		colSingletonId.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getObjectId()).asObject());
		var colSingletonType = new TableColumn<HistorySingletonObject, String>("Singleton Type");
		colSingletonType.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getType().getOriginal()));
		StandardCellFactoryHelper.setFactoryForStringValueColumn(colSingletonType);
		var colSingletonFirstFrame = new TableColumn<HistorySingletonObject, Integer>("First Frame");
		colSingletonFirstFrame.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getFirstFrame()).asObject());
		
		var singletonsTable = new TableView<>(FXCollections.observableList(historyFile.getSingletons()));
		singletonsTable.getColumns().addAll(colSingletonId, colSingletonType, colSingletonFirstFrame);
		singletonsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		
		// singleton state properties table
		
		var singletonPropsUI = new NonVersionedFieldUI(
				GeneralPreferences.getEffective().getHistorySingletonPropsTreeExpanded(),
				"Click a singleton state to view its properties");
		singletonPropsUI.getRootProperty().bind(
				singletonsTable.getSelectionModel().selectedItemProperty().map(f -> f.getTree()));
		
		// top-level layout
		
		Node topNode;
		if (saveHeader == null) {
			topNode = headersTable;
		} else {
			var colModFriendlyName = new TableColumn<X2DlcNamePair, String>("Mod Friendly Name");
			colModFriendlyName.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().friendlyName));
			StandardCellFactoryHelper.setFactoryForStringValueColumn(colModFriendlyName);
			var colModInternalName = new TableColumn<X2DlcNamePair, String>("Mod Internal Name");
			colModInternalName.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().internalName));
			StandardCellFactoryHelper.setFactoryForStringValueColumn(colModInternalName);
			
			var modsTable = new TableView<>(FXCollections.observableList(saveHeader.installedDlcAndMods));
			modsTable.getColumns().addAll(colModFriendlyName, colModInternalName);
			modsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
			
			var topSplit = new SplitPane(headersTable, modsTable);
			topSplit.setOrientation(Orientation.HORIZONTAL);
			
			topNode = topSplit;
		}
		
		var bottomSplit = new SplitPane(singletonsTable, singletonPropsUI.getNode());
		bottomSplit.setOrientation(Orientation.HORIZONTAL);
		
		mainSplit = new SplitPane(topNode, bottomSplit);
		mainSplit.setOrientation(Orientation.VERTICAL);
	}
	
	public Node getNode() {
		return mainSplit;
	}
	
	private static class HeaderPair {
		private final String key;
		private final String value;
		
		public HeaderPair(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}
	
}
