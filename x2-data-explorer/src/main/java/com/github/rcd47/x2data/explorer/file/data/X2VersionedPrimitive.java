package com.github.rcd47.x2data.explorer.file.data;

import java.util.Objects;

public class X2VersionedPrimitive extends X2VersionedDatum<Object> {
	
	public void setValueAt(int frame, Object value) {
		lastFrameTouched = frame;
		
		if (numFrames != 0 && Objects.equals(values[numFrames - 1], value)) {
			// can occur if we're inside a non-delta'd object (dynamic array or native map)
			return;
		}
		
		FieldChangeType change;
		if (value == null) {
			change = FieldChangeType.REMOVED;
		} else if (numFrames == 0 || values[numFrames - 1] == null) {
			change = FieldChangeType.ADDED;
		} else {
			change = FieldChangeType.CHANGED;
		}
		appendChange(frame, change);
		values[numFrames - 1] = value;
		parent.descendantValueSet(frame);
	}

	@Override
	protected Object getValueForTreeNode(int index) {
		return values[index];
	}
	
}
