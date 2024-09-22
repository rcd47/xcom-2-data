package com.github.rcd47.x2data.explorer.prefs;

public enum HistoryObjectsColumn {
	
	ID("Object ID"),
	TYPE("Object Type"),
	SUMMARY("Object Summary"),
	PREV_VERSION("Previous Version"),
	NEXT_VERSION("Next Version"),
	;
	
	private final String headerText;

	private HistoryObjectsColumn(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderText() {
		return headerText;
	}
	
}
