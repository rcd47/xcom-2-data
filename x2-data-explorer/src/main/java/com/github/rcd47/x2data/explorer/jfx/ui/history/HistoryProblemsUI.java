package com.github.rcd47.x2data.explorer.jfx.ui.history;

import com.github.rcd47.x2data.explorer.file.HistoryFile;
import com.github.rcd47.x2data.explorer.file.HistoryFileProblem;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryProblemsUI {
	
	private final TableView<HistoryFileProblem> table;
	
	public HistoryProblemsUI(HistoryFile historyFile) {
		var colFrameNumber = new TableColumn<HistoryFileProblem, Integer>("Frame #");
		colFrameNumber.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getFrame().getNumber()).asObject());
		
		var colObject = new TableColumn<HistoryFileProblem, String>("Object");
		colObject.setCellValueFactory(f -> {
			String description;
			var problem = f.getValue();
			var obj = problem.getObject();
			if (obj == null) {
				var ctx = problem.getContext();
				if (ctx == null) {
					description = "";
				} else {
					description = "frame context (" + ctx.getType().getOriginal() + ")";
				}
			} else {
				description = obj.getObjectId() + " (" + obj.getType().getOriginal() + ")";
			}
			return new ReadOnlyStringWrapper(description);
		});
		
		var colExplanation = new TableColumn<HistoryFileProblem, String>("Explanation");
		colExplanation.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getExplanation()));
		
		table = new TableView<HistoryFileProblem>();
		table.getColumns().add(colFrameNumber);
		table.getColumns().add(colObject);
		table.getColumns().add(colExplanation);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setItems(FXCollections.observableList(historyFile.getProblems()));
	}

	public Node getNode() {
		return table;
	}
	
}
