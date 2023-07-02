package com.github.rcd47.x2data.lib.history;

public class X2HistoryIndexEntry {
	
	private int arrayIndex;
	private String type;
	private int previousVersionIndex;
	private long position;
	private int length;
	private boolean singletonState;
	private Class<?> mappedType;

	X2HistoryIndexEntry(int arrayIndex, String type, int previousVersionIndex) {
		this.arrayIndex = arrayIndex;
		this.type = type;
		this.previousVersionIndex = previousVersionIndex;
	}

	public int getArrayIndex() {
		return arrayIndex;
	}

	public String getType() {
		return type;
	}

	/**
	 * Get the array index for the previous version of this object.
	 * Returns -1 if this is the first version of the object, or {@link #isSingletonState()} is true.<br>
	 * <br>
	 * IMPORTANT: When {@link #isSingletonState()} is true, only the most recent version of the object is serialized!
	 * As a result, every frame refers to the latest version of the object, not the version when frame was added!
	 */
	public int getPreviousVersionIndex() {
		return previousVersionIndex;
	}

	public long getPosition() {
		return position;
	}

	void setPosition(long position) {
		this.position = position;
	}

	public int getLength() {
		return length;
	}

	void setLength(int length) {
		this.length = length;
	}

	public boolean isSingletonState() {
		return singletonState;
	}

	void setSingletonState(boolean singletonState) {
		this.singletonState = singletonState;
	}

	public Class<?> getMappedType() {
		return mappedType;
	}

	void setMappedType(Class<?> mappedType) {
		this.mappedType = mappedType;
	}
	
}