package com.github.rcd47.x2data.explorer.file;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

public class HistoryFrame implements Comparable<HistoryFrame> {
	
	private final int number;
	private final String timestamp;
	private GameStateContext context;
	private Int2ReferenceOpenHashMap<GameStateObject> objectsCold;
	private Int2ReferenceOpenHashMap<GameStateObject> objectsHot;
	
	public HistoryFrame(int number, String timestamp) {
		this.number = number;
		this.timestamp = timestamp;
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

	public void setContext(GameStateContext context) {
		this.context = context;
	}

	public void setObjectsCold(Int2ReferenceOpenHashMap<GameStateObject> objectsCold) {
		this.objectsCold = objectsCold;
	}

	public void setObjectsHot(Int2ReferenceOpenHashMap<GameStateObject> objectsHot) {
		this.objectsHot = objectsHot;
	}
	
	public Int2ReferenceMap<GameStateObject> getObjectsCombined() {
		return combineMaps(objectsCold, objectsHot);
	}
	
	public GameStateObject getObject(int id) {
		return objectsHot.getOrDefault(id, objectsCold.get(id));
	}

	@Override
	public int compareTo(HistoryFrame o) {
		return Integer.compare(number, o.number);
	}
	
	public static Int2ReferenceOpenHashMap<GameStateObject> combineMaps(
			Int2ReferenceOpenHashMap<GameStateObject> coldMap, Int2ReferenceOpenHashMap<GameStateObject> hotMap) {
		var combinedMap = new Int2ReferenceOpenHashMap<>(coldMap);
		hotMap.int2ReferenceEntrySet().fastForEach(e -> {
			if (e.getValue() == null) {
				combinedMap.remove(e.getIntKey());
			} else {
				combinedMap.put(e.getIntKey(), e.getValue());
			}
		});
		combinedMap.trim();
		return combinedMap;
	}
	
}
