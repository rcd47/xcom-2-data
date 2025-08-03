package com.github.rcd47.x2data.explorer.file.data;

import java.util.Arrays;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public abstract class X2VersionedDatum<T> {
	
	protected X2VersionedDataContainer<?> parent;
	protected int[] frames;
	protected FieldChangeType[] changes;
	protected Object[] values;
	protected int numFrames;
	protected int lastFrameTouched;
	
	public X2VersionedDatum() {
		frames = new int[4];
		changes = new FieldChangeType[4];
		values = new Object[4];
	}
	
	protected void appendChange(int frame, FieldChangeType change) {
		if (numFrames != 0 && frames[numFrames - 1] == frame) {
			return;
		}
		if (frames.length == numFrames) {
			int newLength = numFrames + Math.min(numFrames, 1024);
			frames = Arrays.copyOf(frames, newLength);
			changes = Arrays.copyOf(changes, newLength);
			values = Arrays.copyOf(values, newLength);
		}
		frames[numFrames] = frame;
		changes[numFrames] = change;
		numFrames++;
	}
	
	protected int getIndexForFrame(int frame) {
		int index = Arrays.binarySearch(frames, 0, numFrames, frame);
		return index < 0 ? ((index * -1) - 2) : index;
	}
	
	public void markRemoved(int frame) {
		appendChange(frame, FieldChangeType.REMOVED);
	}

	public void descendantValueSet(int frame) {
		appendChange(frame, changes[numFrames - 1] == FieldChangeType.REMOVED ? FieldChangeType.ADDED : FieldChangeType.CHANGED);
		if (parent != null) {
			parent.descendantValueSet(frame);
		}
	}
	
	public final void parseFinished() {
		if (frames.length != numFrames) {
			frames = Arrays.copyOf(frames, numFrames);
			changes = Arrays.copyOf(changes, numFrames);
			values = Arrays.copyOf(values, numFrames);
			parseFinishedExtra();
		}
	}
	
	protected void parseFinishedExtra() {
		// no-op
	}
	
	public TreeItem<X2VersionedDatumTreeItem> getTreeNodeAt(PrimitiveInterner interner, UnrealName nodeName, int frame, boolean onlyModified) {
		var index = getIndexForFrame(frame);
		if (index < 0) {
			return null;
		}
		var frameAtIndex = frames[index];
		if (onlyModified && frameAtIndex != frame) {
			return null;
		}
		var change = changes[index];
		if (frameAtIndex != frame) {
			if (change == FieldChangeType.REMOVED) {
				return null;
			}
			change = FieldChangeType.NONE;
		}
		
		var hasPrev = index != 0;
		var hasNext = index != numFrames - 1;
		var treeItem = new TreeItem<X2VersionedDatumTreeItem>(new X2VersionedDatumTreeItem(
				nodeName,
				getValueForTreeNode(index),
				change,
				hasPrev ? getValueForTreeNode(index - 1) : null,
				hasNext ? getValueForTreeNode(index + 1) : null,
				hasPrev ? frames[index - 1] : Integer.MIN_VALUE,
				hasNext ? frames[index + 1] : Integer.MAX_VALUE));
		addChildrenToTreeNode(interner, frame, onlyModified, treeItem.getChildren());
		return treeItem;
	}
	
	protected Object getValueForTreeNode(int index) {
		return null;
	}
	
	protected void addChildrenToTreeNode(
			PrimitiveInterner interner, int frame, boolean onlyModified, ObservableList<TreeItem<X2VersionedDatumTreeItem>> treeChildren) {
		// no-op
	}
	
	@SuppressWarnings("unchecked")
	public T getValueAt(int frame) {
		var index = getIndexForFrame(frame);
		return index < 0 ? null : (T) values[getIndexForFrame(frame)];
	}
	
}
