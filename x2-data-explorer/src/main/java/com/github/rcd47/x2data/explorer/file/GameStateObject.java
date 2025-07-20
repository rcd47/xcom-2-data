package com.github.rcd47.x2data.explorer.file;

import java.util.List;

import com.github.rcd47.x2data.explorer.file.data.PrimitiveInterner;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedMap;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.google.common.base.Throwables;

import groovy.lang.Script;

public class GameStateObject implements ISizedObject {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	private static final UnrealName REMOVED = new UnrealName("bRemoved");
	
	private final int sizeInFile;
	private final int objectId;
	private final boolean removed; // note that it is possible for an object to be added and removed in the same state
	private final UnrealName type;
	private final String summary;
	private final X2VersionedMap fields;
	private final HistoryFrame frame;
	private final GameStateObject previousVersion;
	private GameStateObject nextVersion;
	
	public GameStateObject(int sizeInFile, GameStateObject previousVersion, X2VersionedMap fields,
			UnrealName type, HistoryFrame frame, PrimitiveInterner interner, Script summarizer,
			List<HistoryFileProblem> problemsDetected) {
		this.sizeInFile = sizeInFile;
		this.type = type;
		this.fields = fields;
		this.frame = frame;
		this.previousVersion = previousVersion;
		
		if (previousVersion != null) {
			previousVersion.nextVersion = this;
		}
		
		var currentVersion = fields.getValueAt(frame.getNumber());
		objectId = (int) currentVersion.get(OBJECT_ID);
		removed = Boolean.TRUE.equals(currentVersion.get(REMOVED));
		
		String summaryTemp;
		try {
			synchronized (summarizer) {
				summarizer.getBinding().setProperty("gso", this);
				summaryTemp = (String) summarizer.run();
			}
		} catch (Exception e) {
			summaryTemp = null;
			problemsDetected.add(new HistoryFileProblem(
					frame, null, this, "Summary script failed. Stack trace:\n" + Throwables.getStackTraceAsString(e)));
		}
		summary = summaryTemp == null ? null : interner.internString(summaryTemp);
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return fields.getValueAt(frame.getNumber()).get(name);
	}
	
	@Override
	public int getSizeInFile() {
		return sizeInFile;
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

	public X2VersionedMap getFields() {
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
	
}
