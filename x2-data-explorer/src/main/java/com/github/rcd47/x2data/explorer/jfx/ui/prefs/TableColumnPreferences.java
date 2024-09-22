package com.github.rcd47.x2data.explorer.jfx.ui.prefs;

import javafx.beans.property.SimpleBooleanProperty;

public class TableColumnPreferences<T> {
	
	private final T column;
	private final SimpleBooleanProperty visible;
	
	public TableColumnPreferences(T column, boolean visible) {
		this.column = column;
		this.visible = new SimpleBooleanProperty(visible);
	}
	
	public TableColumnPreferences<T> duplicate() {
		return new TableColumnPreferences<>(column, visible.get());
	}

	public T getColumn() {
		return column;
	}

	public SimpleBooleanProperty getVisible() {
		return visible;
	}
	
}
