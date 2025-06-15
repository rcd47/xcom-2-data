package com.github.rcd47.x2data.explorer.prefs;

public enum HistoryFileTab {
	
	GENERAL("General"),
	FRAMES("Frames"),
	BLOAT_ANALYSIS("Bloat Analysis"),
	PROBLEMS("Problems"),
	;
	
	private final String tabTitle;

	private HistoryFileTab(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	public String getTabTitle() {
		return tabTitle;
	}
	
}
