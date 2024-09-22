package com.github.rcd47.x2data.explorer.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.scene.control.TreeItem;

public class NonVersionedField {
	
	private final Object value;
	private final Map<UnrealName, NonVersionedField> children;
	
	public NonVersionedField(Object in) {
		if (in instanceof GenericObject object) {
			value = null;
			children = new HashMap<>();
			object.properties.forEach((k, v) -> children.put(k, new NonVersionedField(v)));
		} else if (in instanceof List<?> list) {
			value = null;
			children = new HashMap<>();
			for (int i = 0; i < list.size(); i++) {
				children.put(new UnrealName(Integer.toString(i)), new NonVersionedField(list.get(i)));
			}
		} else if (in instanceof Map<?, ?> map) {
			value = null;
			children = new HashMap<>();
			map.forEach((k, v) -> children.put(UnrealName.from(k), new NonVersionedField(v)));
		} else {
			value = in;
			children = null;
		}
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return children == null ? null : children.get(new UnrealName(name));
	}
	
	public Object getValue() {
		return value;
	}

	public Map<UnrealName, NonVersionedField> getChildren() {
		return children;
	}
	
	public static TreeItem<Entry<UnrealName, NonVersionedField>> convertToTreeItems(Map<UnrealName, NonVersionedField> fields) {
		var root = new TreeItem<Entry<UnrealName, NonVersionedField>>();
		var rootChildren = root.getChildren();
		fields.entrySet()
				.stream()
				.sorted((a, b) -> a.getKey().compareTo(b.getKey()))
				.forEachOrdered(e -> rootChildren.add(convertToTreeItem(e)));
		return root;
	}
	
	private static TreeItem<Entry<UnrealName, NonVersionedField>> convertToTreeItem(Entry<UnrealName, NonVersionedField> entry) {
		var item = new TreeItem<Entry<UnrealName, NonVersionedField>>(entry);
		var fieldChildren = entry.getValue().getChildren();
		if (fieldChildren != null) {
			var itemChildren = item.getChildren();
			fieldChildren
					.entrySet()
					.stream()
					.sorted((a, b) -> a.getKey().compareTo(b.getKey()))
					.forEachOrdered(e -> itemChildren.add(convertToTreeItem(e)));
		}
		return item;
	}
	
}
