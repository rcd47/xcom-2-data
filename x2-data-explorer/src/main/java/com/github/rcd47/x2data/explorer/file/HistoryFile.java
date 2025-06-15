package com.github.rcd47.x2data.explorer.file;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

public class HistoryFile {
	
	private final XComGameStateHistory history;
	private final List<HistoryFrame> frames;
	private final List<HistorySingletonObject> singletons;
	private final List<HistoryFileProblem> problems;
	private final List<UnrealName> contextTypes;
	private final List<UnrealName> objectTypes;
	
	public HistoryFile(XComGameStateHistory history, List<HistoryFrame> frames, List<HistorySingletonObject> singletons,
			List<HistoryFileProblem> problems, List<UnrealName> contextTypes, List<UnrealName> objectTypes) {
		this.history = history;
		this.frames = frames;
		this.singletons = singletons;
		this.problems = problems;
		this.contextTypes = contextTypes;
		this.objectTypes = objectTypes;
	}

	public XComGameStateHistory getHistory() {
		return history;
	}

	public List<HistoryFrame> getFrames() {
		return frames;
	}

	public List<HistorySingletonObject> getSingletons() {
		return singletons;
	}

	public List<HistoryFileProblem> getProblems() {
		return problems;
	}

	public List<UnrealName> getContextTypes() {
		return contextTypes;
	}

	public List<UnrealName> getObjectTypes() {
		return objectTypes;
	}
	
}
