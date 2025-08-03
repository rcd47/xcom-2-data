package com.github.rcd47.x2data.explorer.file.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class X2VersionedStaticArray extends X2VersionedDataContainer<List<Object>> {

	private ArrayList<X2VersionedDatum<?>> elements;
	
	public X2VersionedStaticArray(int frame, int size) {
		super(frame);
		elements = new ArrayList<>(size);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends X2VersionedDatum<?>> T getOrCreateElement(int frame, int index, Supplier<T> creator) {
		for (int i = elements.size(); i <= index; i++) {
			elements.add(null);
		}
		var element = elements.get(index);
		if (element == null) {
			element = creator.get();
			element.parent = this;
			elements.set(index, element);
		}
		element.lastFrameTouched = frame;
		return (T) element;
	}

	@Override
	protected void createFrameValue(int frame) {
		var frameValue = new ArrayList<>(elements.size());
		for (var element : elements) {
			frameValue.add(element == null ? null : element.getValueAt(frame));
		}
		values[numFrames - 1] = frameValue;
	}

	@Override
	protected void parseFinishedExtra() {
		elements.trimToSize();
		for (var element : elements) {
			if (element != null) {
				element.parseFinished();
			}
		}
	}

	@Override
	protected void addChildrenToTreeNode(
			PrimitiveInterner interner, int frame, boolean onlyModified, ObservableList<TreeItem<X2VersionedDatumTreeItem>> treeChildren) {
		for (int i = 0; i < elements.size(); i++) {
			var element = elements.get(i);
			if (element != null) {
				var childNode = elements.get(i).getTreeNodeAt(interner, interner.internTreeNodeArrayIndex(i), frame, onlyModified);
				if (childNode != null) {
					treeChildren.add(childNode);
				}
			}
		}
	}

	@Override
	protected Iterable<X2VersionedDatum<?>> getChildren() {
		return elements;
	}

}
