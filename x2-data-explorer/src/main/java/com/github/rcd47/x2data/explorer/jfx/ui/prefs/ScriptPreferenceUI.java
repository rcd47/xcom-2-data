package com.github.rcd47.x2data.explorer.jfx.ui.prefs;

import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import com.github.rcd47.x2data.explorer.prefs.script.ScriptPreference;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScriptPreferenceUI {
	
	public ScriptPreferenceUI(String dialogTitle, ScriptPreference script) {
		var dialog = new Stage(StageStyle.DECORATED);
		
		var textArea = new TextArea(script.getSource());
		textArea.setPrefColumnCount(100);
		textArea.setPrefRowCount(30);
		VBox.setVgrow(textArea, Priority.ALWAYS);
		
		var applyButton = new Button("Apply");
		applyButton.setOnAction(_ -> {
			try {
				script.setSource(textArea.getText());
				dialog.close();
			} catch (CompilationFailedException | IOException e) {
				var text = new Text(e.toString());
				text.setFill(Color.RED);
				
				var alert = new Alert(AlertType.ERROR);
				alert.setGraphic(text);
				alert.setTitle("Script Error");
				alert.show();
			}
		});
		
		var resetButton = new Button("Reset to defaults");
		resetButton.setOnAction(_ -> textArea.setText(script.getDefaultSource()));
		
		var cancelButton = new Button("Cancel");
		cancelButton.setOnAction(_ -> dialog.close());
		
		var buttonsBox = new HBox(10, applyButton, resetButton, cancelButton);
		buttonsBox.setAlignment(Pos.CENTER);
		
		var vbox = new VBox(10, textArea, buttonsBox);
		vbox.setPadding(new Insets(10));
		vbox.styleProperty().bind(GeneralPreferences.getEffective().getFontSize().map(s -> "-fx-font-size: " + s + "pt"));
		
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setScene(new Scene(vbox));
		dialog.setTitle(dialogTitle);
		dialog.show();
	}
	
}
