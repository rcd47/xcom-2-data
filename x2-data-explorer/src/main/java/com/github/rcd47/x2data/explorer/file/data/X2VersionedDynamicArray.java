package com.github.rcd47.x2data.explorer.file.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class X2VersionedDynamicArray extends X2VersionedDataContainer<List<Object>> {

	private ArrayList<X2VersionedDatum<?>> elements;
	private int nextIndex;
	
	public X2VersionedDynamicArray(int frame, int size) {
		super(frame);
		elements = new ArrayList<>(size);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends X2VersionedDatum<?>> T getOrCreateElement(int frame, Supplier<T> creator) {
		if (elements.size() == nextIndex) {
			var newElement = creator.get();
			newElement.parent = this;
			elements.add(newElement);
		}
		var element = elements.get(nextIndex++);
		element.lastFrameTouched = frame;
		return (T) element;
	}

	@Override
	public void frameFinished(int frame, boolean deltaDisabled) {
		super.frameFinished(frame, deltaDisabled);
		nextIndex = 0;
	}

	@Override
	protected void createFrameValue(int frame) {
		var frameValue = new ArrayList<>(nextIndex);
		for (int i = 0; i < nextIndex; i++) {
			frameValue.add(elements.get(i).getValueAt(frame));
		}
		values[numFrames - 1] = frameValue;
	}

	@Override
	protected void parseFinishedExtra() {
		elements.trimToSize();
		for (var element : elements) {
			element.parseFinished();
		}
	}

	@Override
	protected void addChildrenToTreeNode(
			PrimitiveInterner interner, int frame, boolean onlyModified, ObservableList<TreeItem<X2VersionedDatumTreeItem>> treeChildren) {
		for (int i = 0; i < elements.size(); i++) {
			var childNode = elements.get(i).getTreeNodeAt(interner, interner.internTreeNodeArrayIndex(i), frame, onlyModified);
			if (childNode != null) {
				treeChildren.add(childNode);
			}
		}
	}

	@Override
	protected Iterable<X2VersionedDatum<?>> getChildren() {
		return elements;
	}

}
