package com.github.rcd47.x2data.explorer.file;

import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.scene.control.TreeItem;

public class GameStateObjectField {
	
	private final UnrealName name;
	private final Object value;
	private final Map<UnrealName, GameStateObjectField> children;
	private final GameStateObject lastChangedAt;
	private final GameStateObjectField previousValue;
	private GameStateObjectField nextValue;
	
	public GameStateObjectField(UnrealName name, Object value, Map<UnrealName, GameStateObjectField> children,
			GameStateObject lastChangedAt, GameStateObjectField previousValue) {
		if (previousValue != null) {
			previousValue.nextValue = this;
		}
		
		this.name = name;
		this.value = value;
		this.children = children;
		this.lastChangedAt = lastChangedAt;
		this.previousValue = previousValue;
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return children == null ? null : children.get(new UnrealName(name));
	}
	
	public boolean isTombstone() {
		return value == null && (children == null || children.values().stream().allMatch(c -> c.isTombstone()));
	}
	
	public TreeItem<GameStateObjectFieldTreeNode> asTreeNode(GameStateObject stateObject, boolean onlyModified) {
		if (onlyModified && lastChangedAt != stateObject) { // deliberate identity comparison
			return null;
		}
		
		if (children == null) {
			FieldChangeType changeType;
			if (lastChangedAt != stateObject) { // deliberate identity comparison
				changeType = FieldChangeType.NONE;
			} else if (value == null) {
				changeType = FieldChangeType.REMOVED;
			} else if (previousValue == null || previousValue.isTombstone()) {
				changeType = FieldChangeType.ADDED;
			} else {
				changeType = FieldChangeType.CHANGED;
			}
			return new TreeItem<GameStateObjectFieldTreeNode>(new GameStateObjectFieldTreeNode(name, value, changeType, previousValue, nextValue));
		}
		
		var changeType = FieldChangeType.NONE;
		var node = new TreeItem<GameStateObjectFieldTreeNode>();
		var nodeChildren = node.getChildren();
		var childNodes = children
				.values()
				.stream()
				.sorted((a, b) -> a.name.compareTo(b.name))
				.map(f -> f.asTreeNode(stateObject, onlyModified))
				.iterator();
		while (childNodes.hasNext()) {
			var childNode = childNodes.next();
			if (childNode == null) { // we want only modified and child was not modified
				if (changeType != FieldChangeType.NONE) {
					changeType = FieldChangeType.CHANGED;
				}
			} else {
				nodeChildren.add(childNode);
				var childChangeType = childNode.getValue().getChangeType();
				if (changeType == FieldChangeType.NONE) {
					changeType = childChangeType;
				} else if (changeType != childChangeType) {
					changeType = FieldChangeType.CHANGED;
				}
			}
		}
		node.setValue(new GameStateObjectFieldTreeNode(name, value, changeType, previousValue, nextValue));
		return node;
	}

	public UnrealName getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public Map<UnrealName, GameStateObjectField> getChildren() {
		return children;
	}

	public GameStateObject getLastChangedAt() {
		return lastChangedAt;
	}

	public GameStateObjectField getPreviousValue() {
		return previousValue;
	}

	public GameStateObjectField getNextValue() {
		return nextValue;
	}
	
}
