package com.github.rcd47.x2data.explorer.jfx.ui;

import com.google.common.base.Throwables;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ProgressUtils {
	
	public static Node createProgressUi(Task<?> task) {
		var progressBar = new ProgressBar();
		progressBar.setPrefWidth(400);
		progressBar.progressProperty().bind(task.progressProperty());
		
		var progressText = new Text();
		progressText.textProperty().bind(task.messageProperty());
		
		var vbox = new VBox(5, progressBar, progressText);
		vbox.setAlignment(Pos.BASELINE_CENTER);
		vbox.setPadding(new Insets(10));
		
		return vbox;
	}
	
	public static Node createTaskFailureUi(Throwable t) {
		var text = new Text(Throwables.getStackTraceAsString(t));
		text.setFill(Color.RED);
		
		var copyMenuItem = new MenuItem("Copy to clipboard");
		copyMenuItem.setOnAction(_ -> {
			var content = new ClipboardContent();
			content.putString(text.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
		
		var scrollPane = new ScrollPane(text);
		scrollPane.setPadding(new Insets(10));
		scrollPane.setContextMenu(new ContextMenu(copyMenuItem));
		
		return scrollPane;
	}
	
}
