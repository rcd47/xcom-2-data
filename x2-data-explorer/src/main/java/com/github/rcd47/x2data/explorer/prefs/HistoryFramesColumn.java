package com.github.rcd47.x2data.explorer.prefs;

public enum HistoryFramesColumn {
	
	FRAME_NUMBER("Frame #"),
	TIMESTAMP("Timestamp"),
	CONTEXT_TYPE("Context Type"),
	CONTEXT_SUMMARY("Context Summary"),
	INTERRUPT_STATUS("Interrupt Status"),
	RESUMED_FROM("Resumed From"),
	RESUMED_BY("Resumed By"),
	INTERRUPTED("Interrupted"),
	;
	
	private final String headerText;

	private HistoryFramesColumn(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderText() {
		return headerText;
	}
	
}
