package com.github.rcd47.x2data.explorer.file.data;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class X2VersionedMap extends X2VersionedDataContainer<Map<Object, Object>> {

	private Object2ReferenceOpenHashMap<Object, X2VersionedDatum<?>> children; // key type is not UnrealName so that we can handle native maps
	
	public X2VersionedMap(int frame) {
		super(frame);
		children = new Object2ReferenceOpenHashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends X2VersionedDatum<?>> T getOrCreateChild(int frame, Object key, int staticArrayIndex, Supplier<T> creator) {
		var datum = children.get(key);
		
		if (staticArrayIndex == 0) {
			if (datum == null) {
				datum = creator.get();
				datum.parent = this;
				children.put(key, datum);
			}
			if (!(datum instanceof X2VersionedStaticArray)) {
				datum.lastFrameTouched = frame;
				return (T) datum;
			}
		}
		
		X2VersionedStaticArray array;
		if (datum instanceof X2VersionedStaticArray datumArray) {
			array = datumArray;
		} else { // could be null, primitive, or struct
			array = new X2VersionedStaticArray(frame, staticArrayIndex + 1);
			array.parent = this;
			children.put(key, array);
			if (datum != null) {
				var datumFinal = datum;
				array.getOrCreateElement(frame, 0, () -> datumFinal); // will always create
			}
		}
		array.lastFrameTouched = frame;
		return array.getOrCreateElement(frame, staticArrayIndex, creator);
	}

	@Override
	public void frameFinished(int frame, boolean deltaDisabled) {
		for (var child : getChildren()) {
			if (child instanceof X2VersionedStaticArray array) {
				array.frameFinished(frame, deltaDisabled);
			}
		}
		super.frameFinished(frame, deltaDisabled);
	}

	@Override
	protected void createFrameValue(int frame) {
		var plainMap = new Object2ReferenceOpenCustomHashMap<Object, Object>(children.size(), new X2VersionedMapChildrenStrategy());
		children.forEach((k, v) -> {
			var plainValue = v.getValueAt(frame);
			if (plainValue != null) {
				plainMap.put(k, plainValue);
			}
		});
		plainMap.trim();
		values[numFrames - 1] = plainMap;
	}

	@Override
	protected void parseFinishedExtra() {
		children.trim();
		for (var child : children.values()) {
			child.parseFinished();
		}
	}

	@Override
	protected void addChildrenToTreeNode(
			PrimitiveInterner interner, int frame, boolean onlyModified, ObservableList<TreeItem<X2VersionedDatumTreeItem>> treeChildren) {
		children.forEach((k, v) -> {
			var childNode = v.getTreeNodeAt(interner, interner.internTreeNodeMapKey(k), frame, onlyModified);
			if (childNode != null) {
				treeChildren.add(childNode);
			}
		});
		treeChildren.sort(Comparator.comparing(TreeItem::getValue));
	}

	@Override
	protected Iterable<X2VersionedDatum<?>> getChildren() {
		return children.values();
	}

}
