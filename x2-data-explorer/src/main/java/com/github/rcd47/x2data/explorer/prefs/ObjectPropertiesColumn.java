package com.github.rcd47.x2data.explorer.prefs;

public enum ObjectPropertiesColumn {
	
	NAME("Property"),
	PREV_VALUE("Previous Value"),
	CURRENT_VALUE("Current Value"),
	NEXT_VALUE("Next Value"),
	PREV_FRAME("Previous Frame"),
	NEXT_FRAME("Next Frame"),
	;
	
	private final String headerText;

	private ObjectPropertiesColumn(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderText() {
		return headerText;
	}
	
}
