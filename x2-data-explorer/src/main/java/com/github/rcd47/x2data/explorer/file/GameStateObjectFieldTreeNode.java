package com.github.rcd47.x2data.explorer.file;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class GameStateObjectFieldTreeNode implements Comparable<GameStateObjectFieldTreeNode> {
	
	private final UnrealName name;
	private final Object value;
	private final FieldChangeType changeType;
	private final GameStateObjectField previousValue;
	private final GameStateObjectField nextValue;
	
	public GameStateObjectFieldTreeNode(UnrealName name, Object value, FieldChangeType changeType,
			GameStateObjectField previousValue, GameStateObjectField nextValue) {
		this.name = name;
		this.value = value;
		this.changeType = changeType;
		this.previousValue = previousValue;
		this.nextValue = nextValue;
	}

	@Override
	public int compareTo(GameStateObjectFieldTreeNode o) {
		return name.compareTo(o.name);
	}

	public UnrealName getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public FieldChangeType getChangeType() {
		return changeType;
	}

	public GameStateObjectField getPreviousValue() {
		return previousValue;
	}

	public GameStateObjectField getNextValue() {
		return nextValue;
	}
	
}
