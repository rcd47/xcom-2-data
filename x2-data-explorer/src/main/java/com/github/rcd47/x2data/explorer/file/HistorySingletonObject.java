package com.github.rcd47.x2data.explorer.file;

import com.github.rcd47.x2data.explorer.file.data.PrimitiveInterner;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedDatumTreeItem;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedMap;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.scene.control.TreeItem;

public class HistorySingletonObject implements ISizedObject {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	
	private final int sizeInFile;
	private final int firstFrame;
	private final int objectId;
	private final UnrealName type;
	private final TreeItem<X2VersionedDatumTreeItem> tree;
	
	public HistorySingletonObject(int sizeInFile, int firstFrame, UnrealName type, X2VersionedMap properties, PrimitiveInterner interner) {
		this.sizeInFile = sizeInFile;
		this.firstFrame = firstFrame;
		this.type = type;
		objectId = (int) properties.getValueAt(0).get(OBJECT_ID);
		tree = properties.getTreeNodeAt(interner, null, 0, false);
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

	public TreeItem<X2VersionedDatumTreeItem> getTree() {
		return tree;
	}
	
}
