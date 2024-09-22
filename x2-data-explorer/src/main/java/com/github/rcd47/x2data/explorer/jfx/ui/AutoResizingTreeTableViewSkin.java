package com.github.rcd47.x2data.explorer.jfx.ui;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.skin.TreeTableViewSkin;

public class AutoResizingTreeTableViewSkin<T> extends TreeTableViewSkin<T> {

	public AutoResizingTreeTableViewSkin(TreeTableView<T> control) {
		super(control);
		control.rootProperty().addListener((obs, oldVal, newVal) -> {
			for (var header : getTableHeaderRow().getRootHeader().getColumnHeaders()) {
				// this whole damn hack is just because resizeColumnToFitContent() is not public
				// 30 is what TableColumnHeader#updateScene() uses
				((TableColumnHeaderHack) header).resizeColumnToFitContent(30);
			}
		});
	}

	@Override
	protected TableHeaderRow createTableHeaderRow() {
		return new TableHeaderRowHack(this);
	}
	
	private static class TableHeaderRowHack extends TableHeaderRow {
		public TableHeaderRowHack(@SuppressWarnings("rawtypes") TableViewSkinBase skin) {
			super(skin);
		}

		@Override
		protected NestedTableColumnHeader createRootHeader() {
			return new NestedTableColumnHeaderHack(null);
		}
	}
	
	private static class NestedTableColumnHeaderHack extends NestedTableColumnHeader {
		public NestedTableColumnHeaderHack(@SuppressWarnings("rawtypes") TableColumnBase tc) {
			super(tc);
		}

		@Override
		protected TableColumnHeader createTableColumnHeader(@SuppressWarnings("rawtypes") TableColumnBase col) {
			return new TableColumnHeaderHack(col);
		}
		
	}
	
	public static class TableColumnHeaderHack extends TableColumnHeader {
		public TableColumnHeaderHack(@SuppressWarnings("rawtypes") TableColumnBase tc) {
			super(tc);
		}

		@Override
		public void resizeColumnToFitContent(int maxRows) {
			super.resizeColumnToFitContent(maxRows);
		}
	}
	
}