package com.github.rcd47.x2data.explorer.file;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

public class HistoryFile {
	
	private final XComGameStateHistory history;
	private final List<HistoryFrame> frames;
	private final List<HistorySingletonObject> singletons;
	
	public HistoryFile(XComGameStateHistory history, List<HistoryFrame> frames,
			List<HistorySingletonObject> singletons) {
		this.history = history;
		this.frames = frames;
		this.singletons = singletons;
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
	
}
