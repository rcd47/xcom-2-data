package com.github.rcd47.x2data.explorer.file;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

public class HistoryFile {
	
	private final XComGameStateHistory history;
	private final List<HistoryFrame> frames;
	private final List<HistorySingletonObject> singletons;
	private final List<HistoryFileProblem> problems;
	
	public HistoryFile(XComGameStateHistory history, List<HistoryFrame> frames, List<HistorySingletonObject> singletons,
			List<HistoryFileProblem> problems) {
		this.history = history;
		this.frames = frames;
		this.singletons = singletons;
		this.problems = problems;
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
	
}
