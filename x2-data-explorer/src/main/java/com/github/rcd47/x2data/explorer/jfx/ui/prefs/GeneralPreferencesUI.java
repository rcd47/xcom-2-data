package com.github.rcd47.x2data.explorer.jfx.ui.prefs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.rcd47.x2data.explorer.prefs.HistoryFileTab;
import com.github.rcd47.x2data.explorer.prefs.HistoryFramesColumn;
import com.github.rcd47.x2data.explorer.prefs.HistoryObjectsColumn;
import com.github.rcd47.x2data.explorer.prefs.ObjectPropertiesColumn;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class GeneralPreferencesUI {
	
	private static final Logger L = LogManager.getLogger(GeneralPreferencesUI.class);
	private static final DataFormat DUMMY_FORMAT = new DataFormat("application/x-dummy-format");
	
	public GeneralPreferencesUI() {
		var prefs = GeneralPreferences.getEffective().duplicate();
		
		var dialog = new Stage(StageStyle.DECORATED);
		
		var gridPane = new GridPane(10, 10);
		gridPane.setPadding(new Insets(10));
		// not binding the font size because changing on-the-fly makes the dialog harder to use
		gridPane.setStyle("-fx-font-size: " + prefs.getFontSize().get() + "pt");
		
		var fontSize = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 64, prefs.getFontSize().get()));
		prefs.getFontSize().asObject().bindBidirectional(fontSize.getValueFactory().valueProperty());
		
		var historyFileDefaultTab = new ChoiceBox<>(FXCollections.observableArrayList(HistoryFileTab.values()));
		historyFileDefaultTab.valueProperty().bindBidirectional(prefs.getHistoryFileDefaultTab());
		historyFileDefaultTab.setConverter(new HistoryFileTabConverter());
		
		var saveFileDefaultTab = new ChoiceBox<>(FXCollections.observableArrayList(HistoryFileTab.values()));
		saveFileDefaultTab.valueProperty().bindBidirectional(prefs.getSaveFileDefaultTab());
		saveFileDefaultTab.setConverter(new HistoryFileTabConverter());
		
		var histObjPropsTreeExpanded = new CheckBox("History Object");
		histObjPropsTreeExpanded.selectedProperty().bindBidirectional(prefs.getHistoryObjPropsTreeExpanded());
		
		var histContextTreeExpanded = new CheckBox("History Context");
		histContextTreeExpanded.selectedProperty().bindBidirectional(prefs.getHistoryContextPropsTreeExpanded());
		
		var histSingletonTreeExpanded = new CheckBox("History Singleton");
		histSingletonTreeExpanded.selectedProperty().bindBidirectional(prefs.getHistorySingletonPropsTreeExpanded());
		
		var bsoTreeExpanded = new CheckBox("BasicSaveObject");
		bsoTreeExpanded.selectedProperty().bindBidirectional(prefs.getBsoTreeExpanded());
		
		var treesExpanded = new HBox(10, histObjPropsTreeExpanded, histContextTreeExpanded, histSingletonTreeExpanded, bsoTreeExpanded);
		
		var applyButton = new Button("Apply");
		applyButton.setOnAction(_ -> {
			try {
				prefs.applyChanges();
			} catch (IOException e) {
				L.error("Failed to save general prefs file", e);
			}
			
			dialog.close();
		});
		
		var resetButton = new Button("Reset to defaults");
		resetButton.setOnAction(_ -> prefs.resetToDefaults());
		
		var cancelButton = new Button("Cancel");
		cancelButton.setOnAction(_ -> dialog.close());
		
		var buttonsBox = new HBox(10, applyButton, resetButton, cancelButton);
		
		gridPane.addRow(0, createLabel("Font Size"), fontSize);
		gridPane.addRow(1, createLabel("History File Default Tab"), historyFileDefaultTab);
		gridPane.addRow(2, createLabel("Save File Default Tab"), saveFileDefaultTab);
		gridPane.addRow(3, createLabel("Tree Expanded By Default"), treesExpanded);
		gridPane.add(new Label("Drag and drop the column names to change the default order"), 1, 4);
		gridPane.addRow(5, createLabel("Frames Table Columns"), createTableColumnList(prefs.getFramesColumns(), HistoryFramesColumn::getHeaderText));
		gridPane.addRow(6, createLabel("Objects Table Columns"), createTableColumnList(prefs.getObjectsColumns(), HistoryObjectsColumn::getHeaderText));
		gridPane.addRow(7, createLabel("Properties Table Columns"), createTableColumnList(prefs.getObjPropsColumns(), ObjectPropertiesColumn::getHeaderText));
		gridPane.add(buttonsBox, 1, 8);
		
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setScene(new Scene(gridPane));
		dialog.setTitle("General Preferences");
		dialog.show();
	}
	
	private static <T> Node createTableColumnList(ObservableList<TableColumnPreferences<T>> columns, Function<T, String> headerGetter) {
		var checkboxes = new HashMap<TableColumnPreferences<T>, CheckBox>();
		var container = new HBox(10);
		
		for (int i = 0; i < columns.size(); i++) {
			var col = columns.get(i);
			var iFinal = i;
			
			var checkbox = new CheckBox(headerGetter.apply(col.getColumn()));
			checkbox.setUserData(col);
			checkbox.selectedProperty().bindBidirectional(col.getVisible());
			
			checkbox.setOnDragDetected(event -> {
				var dragboard = checkbox.startDragAndDrop(TransferMode.MOVE);
				dragboard.setContent(Map.of(DUMMY_FORMAT, "")); // further events are not fired if there's no content
				dragboard.setDragView(checkbox.snapshot(null, null));
				event.consume();
			});
			
			checkbox.setOnMouseDragged(event -> {
				event.setDragDetect(true);
			});
			
			checkbox.setOnDragOver(event -> {
				var source = event.getGestureSource();
				if (!checkbox.equals(source) && checkboxes.containsValue(source)) {
					event.acceptTransferModes(TransferMode.MOVE);
					event.consume();
				}
			});
			
			checkbox.setOnDragDropped(event -> {
				var source = event.getGestureSource();
				if (!checkbox.equals(source) && checkboxes.containsValue(source)) {
					@SuppressWarnings("unchecked")
					var otherCol = (TableColumnPreferences<T>) ((CheckBox) source).getUserData();
					columns.remove(otherCol);
					columns.add(iFinal, otherCol);
					event.setDropCompleted(true);
					event.consume();
				}
			});
			
			checkboxes.put(col, checkbox);
			container.getChildren().add(checkbox);
		}
		
		columns.addListener((ListChangeListener.Change<? extends TableColumnPreferences<T>> event) -> {
			while (event.next()) {
				if (event.wasRemoved()) {
					container.getChildren()
							.remove(event.getFrom(), event.getFrom() + event.getRemovedSize());
				} else if (event.wasAdded()) {
					container.getChildren()
							.addAll(event.getFrom(), event.getAddedSubList().stream().map(checkboxes::get).toList());
				}
			}
		});
		
		return container;
	}
	
	private static Label createLabel(String text) {
		var label = new Label(text);
		label.setStyle("-fx-font-weight: bold");
		GridPane.setHalignment(label, HPos.RIGHT);
		return label;
	}
	
	private static class HistoryFileTabConverter extends StringConverter<HistoryFileTab> {
		@Override
		public String toString(HistoryFileTab object) {
			return object == null ? null : object.getTabTitle();
		}
		
		@Override
		public HistoryFileTab fromString(String string) {
			return string == null ? null : HistoryFileTab.valueOf(string);
		}
	}
	
}
