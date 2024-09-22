package com.github.rcd47.x2data.explorer.file;

import java.util.Map;

public class HistoryFrame implements Comparable<HistoryFrame> {
	
	private final int number;
	private final String timestamp;
	private GameStateContext context;
	private Map<Integer, GameStateObject> objects;
	
	public HistoryFrame(int number, String timestamp) {
		this.number = number;
		this.timestamp = timestamp;
	}
	
	void finish(GameStateContext context, Map<Integer, GameStateObject> objects) {
		this.context = context;
		this.objects = objects;
	}

	public int getNumber() {
		return number;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public GameStateContext getContext() {
		return context;
	}

	public Map<Integer, GameStateObject> getObjects() {
		return objects;
	}

	@Override
	public int compareTo(HistoryFrame o) {
		return Integer.compare(number, o.number);
	}
	
}
