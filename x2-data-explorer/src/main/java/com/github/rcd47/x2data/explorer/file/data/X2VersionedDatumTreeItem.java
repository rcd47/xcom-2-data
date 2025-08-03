package com.github.rcd47.x2data.explorer.file.data;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class X2VersionedDatumTreeItem implements Comparable<X2VersionedDatumTreeItem> {
	
	private final UnrealName name;
	private final Object value;
	private final FieldChangeType changeType;
	private final Object previousValue;
	private final Object nextValue;
	private final int previousFrame;
	private final int nextFrame;
	
	public X2VersionedDatumTreeItem(UnrealName name, Object value, FieldChangeType changeType, Object previousValue,
			Object nextValue, int previousFrame, int nextFrame) {
		this.name = name;
		this.value = value;
		this.changeType = changeType;
		this.previousValue = previousValue;
		this.nextValue = nextValue;
		this.previousFrame = previousFrame;
		this.nextFrame = nextFrame;
	}

	@Override
	public int compareTo(X2VersionedDatumTreeItem o) {
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

	public Object getPreviousValue() {
		return previousValue;
	}

	public Object getNextValue() {
		return nextValue;
	}

	public int getPreviousFrame() {
		return previousFrame;
	}

	public int getNextFrame() {
		return nextFrame;
	}
	
}
