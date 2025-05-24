package com.github.rcd47.x2data.explorer.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.google.common.base.Throwables;

import groovy.lang.Script;
import javafx.scene.control.TreeItem;

public class GameStateObject {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	private static final UnrealName REMOVED = new UnrealName("bRemoved");
	
	private final int objectId;
	private final boolean removed; // note that it is possible for an object to be added and removed in the same state
	private final UnrealName type;
	private final String summary;
	private final Map<UnrealName, GameStateObjectField> fields;
	private final HistoryFrame frame;
	private final GameStateObject previousVersion;
	private GameStateObject nextVersion;
	
	public GameStateObject(Map<Integer, GameStateObject> stateObjects, GenericObject currentVersion, HistoryFrame frame, Script summarizer,
			List<HistoryFileProblem> problemsDetected) {
		this.frame = frame;
		
		objectId = (int) currentVersion.properties.get(OBJECT_ID);
		removed = Boolean.TRUE.equals(currentVersion.properties.get(REMOVED));
		type = currentVersion.type;
		previousVersion = stateObjects.put(objectId, this);
		if (previousVersion == null) {
			fields = diffFields(this, null, currentVersion.properties);
		} else {
			fields = diffFields(this, previousVersion.fields, currentVersion.properties);
			previousVersion.nextVersion = this;
		}
		
		String summaryTemp;
		try {
			summarizer.getBinding().setProperty("gso", this);
			summaryTemp = (String) summarizer.run();
		} catch (Exception e) {
			summaryTemp = "";
			problemsDetected.add(new HistoryFileProblem(
					frame, null, this, "Summary script failed. Stack trace:\n" + Throwables.getStackTraceAsString(e)));
		}
		summary = summaryTemp;
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return fields == null ? null : fields.get(new UnrealName(name));
	}
	
	public TreeItem<GameStateObjectFieldTreeNode> getFieldsAsTreeNode(boolean onlyModified) {
		var root = new TreeItem<GameStateObjectFieldTreeNode>();
		var rootChildren = root.getChildren();
		fields.values()
				.stream()
				.sorted((a, b) -> a.getName().compareTo(b.getName()))
				.map(f -> f.asTreeNode(this, onlyModified))
				.filter(f -> f != null)
				.forEachOrdered(rootChildren::add);
		return root;
	}
	
	public int getObjectId() {
		return objectId;
	}

	public boolean isRemoved() {
		return removed;
	}

	public UnrealName getType() {
		return type;
	}

	public String getSummary() {
		return summary;
	}

	public Map<UnrealName, GameStateObjectField> getFields() {
		return fields;
	}

	public HistoryFrame getFrame() {
		return frame;
	}

	public GameStateObject getPreviousVersion() {
		return previousVersion;
	}

	public GameStateObject getNextVersion() {
		return nextVersion;
	}

	private static Map<UnrealName, GameStateObjectField> diffFields(
			GameStateObject newStateObject, Map<UnrealName, GameStateObjectField> oldFields, Map<UnrealName, Object> newGenericFields) {
		if (oldFields == null) {
			oldFields = Map.of();
		}
		if (newGenericFields == null) {
			newGenericFields = Map.of();
		}
		Set<UnrealName> fieldNames = new HashSet<>(newGenericFields.keySet());
		for (var oldField : oldFields.values()) {
			if (!oldField.isTombstone()) {
				fieldNames.add(oldField.getName());
			}
		}
		
		boolean changed = false;
		Map<UnrealName, GameStateObjectField> newFields = new HashMap<>();
		for (var fieldName : fieldNames) {
			var oldField = oldFields.get(fieldName);
			var newField = diffField(newStateObject, fieldName, oldField, newGenericFields.get(fieldName));
			newFields.put(fieldName, newField);
			if (oldField != newField) { // deliberate identity comparison
				changed = true;
			}
		}
		
		return changed ? newFields : oldFields;
	}
	
	@SuppressWarnings("unchecked")
	private static GameStateObjectField diffField(
			GameStateObject newStateObject, UnrealName fieldName, GameStateObjectField oldField, Object newValue) {
		if (newValue != null) {
			Map<UnrealName, Object> newValueMap;
			if (newValue instanceof List<?> list) {
				newValueMap = new HashMap<>();
				for (int i = 0; i < list.size(); i++) {
					var child = list.get(i);
					if (child != null) { // can be null for static arrays, if child is the default value and thus not serialized
						newValueMap.put(new UnrealName(Integer.toString(i)), child);
					}
				}
			} else if (newValue instanceof GenericObject obj) {
				newValueMap = obj.properties;
			} else if (newValue instanceof Map) {
				newValueMap = (Map<UnrealName, Object>) newValue;
			} else {
				// must be a scalar
				return oldField != null && Objects.equals(oldField.getValue(), newValue) ?
						oldField : new GameStateObjectField(fieldName, newValue, null, newStateObject, oldField);
			}
			
			var oldFieldChildren = oldField == null ? null : oldField.getChildren();
			var newFieldChildren = diffFields(newStateObject, oldFieldChildren, newValueMap);
			return newFieldChildren.equals(oldFieldChildren) ?
					oldField : new GameStateObjectField(fieldName, null, newFieldChildren, newStateObject, oldField);
		}
		
		var oldFieldChildren = oldField.getChildren();
		return new GameStateObjectField(
				fieldName, null,
				oldFieldChildren == null ? null : diffFields(newStateObject, oldFieldChildren, null),
				newStateObject, oldField);
	}
	
}
