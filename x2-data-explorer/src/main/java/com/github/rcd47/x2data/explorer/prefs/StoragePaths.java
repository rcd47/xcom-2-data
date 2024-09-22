package com.github.rcd47.x2data.explorer.prefs;

import java.nio.file.Path;

public interface StoragePaths {
	
	final Path BASE_PATH = Path.of(System.getProperty("user.home")).resolve(".x2-data-explorer");
	final Path GENERAL_PREFERENCES_FILE = BASE_PATH.resolve("generalPrefs.json");
	final Path RUNTIME_STATE_FILE = BASE_PATH.resolve("runtimeState.json");
	final Path STATE_OBJECT_SUMMARY_SCRIPT_FILE = BASE_PATH.resolve("stateObjSummary.groovy");
	final Path CONTEXT_SUMMARY_SCRIPT_FILE = BASE_PATH.resolve("contextSummary.groovy");
	
}
