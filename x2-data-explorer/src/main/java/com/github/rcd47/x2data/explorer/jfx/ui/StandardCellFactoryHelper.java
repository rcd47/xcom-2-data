package com.github.rcd47.x2data.explorer.jfx.ui;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Hex;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

public class StandardCellFactoryHelper {
	
	public static <S> void setFactoryForStringValueColumn(TableColumn<S, String> col) {
		col.setCellFactory(_ -> configureCellForStringValueColumn(new TableCell<>()));
	}
	
	public static <S> void setFactoryForStringValueColumn(TreeTableColumn<S, String> col) {
		col.setCellFactory(_ -> configureCellForStringValueColumn(new TreeTableCell<>()));
	}
	
	private static <C extends IndexedCell<String>> C configureCellForStringValueColumn(C cell) {
		cell.textProperty().bind(cell.itemProperty());
		return configureCellForValueColumn(cell);
	}
	
	public static <S, T> void setFactoryForObjectValueColumn(TreeTableColumn<S, T> col) {
		col.setCellFactory(_ -> {
			var cell = configureCellForValueColumn(new TreeTableCell<S, T>());
			cell.textProperty().bind(cell.itemProperty().map(value -> {
				String valueStringified;
				if (value == null) {
					valueStringified = "";
				} else if (value instanceof UnrealName name) {
					valueStringified = name.getOriginal();
				} else if (value instanceof ByteBuffer buffer) {
					valueStringified = Hex.encodeHexString(buffer, false);
					buffer.rewind();
				} else {
					valueStringified = value.toString();
				}
				return valueStringified;
			}));
			cell.itemProperty().addListener((_, oldVal, newVal) -> {
				if (newVal instanceof ByteBuffer && !(oldVal instanceof ByteBuffer)) {
					cell.getStyleClass().add("unparseableData");
				} else if (!(newVal instanceof ByteBuffer) && oldVal instanceof ByteBuffer) {
					cell.getStyleClass().remove("unparseableData");
				}
			});
			return cell;
		});
	}
	
	public static <C extends IndexedCell<?>> C configureCellForValueColumn(C cell) {
		cell.tooltipProperty().bind(cell.textProperty().map(text -> {
			var tooltip = new Tooltip(text);
			tooltip.setShowDelay(Duration.millis(250));
			tooltip.setShowDuration(Duration.INDEFINITE);
			return tooltip;
		}));
		
		var copyMenuItem = new MenuItem("Copy to clipboard");
		copyMenuItem.setOnAction(_ -> {
			var content = new ClipboardContent();
			content.putString(cell.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
		cell.setContextMenu(new ContextMenu(copyMenuItem));
		
		return cell;
	}
	
}
