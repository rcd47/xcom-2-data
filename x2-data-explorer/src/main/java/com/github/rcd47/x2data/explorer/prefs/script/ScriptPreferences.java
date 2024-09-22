package com.github.rcd47.x2data.explorer.prefs.script;

import com.github.rcd47.x2data.explorer.prefs.StoragePaths;

public interface ScriptPreferences {
	
	final ScriptPreference STATE_OBJECT_SUMMARY = new ScriptPreference(
			StoragePaths.STATE_OBJECT_SUMMARY_SCRIPT_FILE, "/defaultStateObjSummary.groovy");
	final ScriptPreference CONTEXT_SUMMARY = new ScriptPreference(
			StoragePaths.CONTEXT_SUMMARY_SCRIPT_FILE, "/defaultContextSummary.groovy");
	
}
