package com.github.rcd47.x2data.explorer.file;

import java.util.HashMap;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class HistorySingletonObject implements ISizedObject {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	
	private final int sizeInFile;
	private final int firstFrame;
	private final int objectId;
	private final UnrealName type;
	private final Map<UnrealName, NonVersionedField> fields;
	
	public HistorySingletonObject(int sizeInFile, int firstFrame, GenericObject object) {
		this.sizeInFile = sizeInFile;
		this.firstFrame = firstFrame;
		objectId = (int) object.properties.get(OBJECT_ID);
		type = object.type;
		fields = new HashMap<>();
		object.properties.forEach((k, v) -> fields.put(k, new NonVersionedField(v)));
	}

	@Override
	public int getSizeInFile() {
		return sizeInFile;
	}

	public int getFirstFrame() {
		return firstFrame;
	}

	public int getObjectId() {
		return objectId;
	}

	public UnrealName getType() {
		return type;
	}

	public Map<UnrealName, NonVersionedField> getFields() {
		return fields;
	}
	
}
