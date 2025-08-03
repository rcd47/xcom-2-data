package com.github.rcd47.x2data.explorer.file;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.rcd47.x2data.explorer.file.data.PrimitiveInterner;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedDatumTreeItem;
import com.github.rcd47.x2data.explorer.file.data.X2VersionedMap;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.EInterruptionStatus;
import com.google.common.base.Throwables;

import groovy.lang.Script;
import javafx.scene.control.TreeItem;

public class GameStateContext implements ISizedObject {
	
	private static final UnrealName INTERRUPTION_HISTORY_INDEX = new UnrealName("InterruptionHistoryIndex");
	private static final UnrealName HISTORY_INDEX_INTERRUPTED_BY_SELF = new UnrealName("HistoryIndexInterruptedBySelf");
	private static final UnrealName INTERRUPTION_STATUS = new UnrealName("InterruptionStatus");
	private static final Map<UnrealName, EInterruptionStatus> STATUS_MAP =
			Arrays.stream(EInterruptionStatus.values()).collect(Collectors.toMap(s -> new UnrealName(s.name()), Function.identity()));
	
	private final int sizeInFile;
	private final UnrealName type;
	private final X2VersionedMap properties;
	private final HistoryFrame frame;
	private final String summary;
	private final EInterruptionStatus interruptionStatus;
	private final HistoryFrame resumedFrom;
	private final HistoryFrame interruptedByThis;
	private HistoryFrame resumedBy;
	
	public GameStateContext(int sizeInFile, UnrealName type, X2VersionedMap properties, HistoryFrame frame,
			int frameOffset, HistoryFrame[] frames, PrimitiveInterner interner, Script summarizer,
			List<HistoryFileProblem> problemsDetected) {
		this.sizeInFile = sizeInFile;
		this.type = type;
		this.properties = properties;
		this.frame = frame;
		
		var fields = properties.getValueAt(0);
		
		interruptionStatus = Optional
				.ofNullable(fields.get(INTERRUPTION_STATUS))
				.map(STATUS_MAP::get)
				.orElse(EInterruptionStatus.eInterruptionStatus_None);
		
		String summaryTemp;
		try {
			synchronized (summarizer) {
				summarizer.getBinding().setProperty("ctx", this);
				summaryTemp = (String) summarizer.run();
			}
		} catch (Exception e) {
			summaryTemp = null;
			problemsDetected.add(new HistoryFileProblem(
					frame, this, null, "Summary script failed. Stack trace:\n" + Throwables.getStackTraceAsString(e)));
		}
		summary = summaryTemp == null ? null : interner.internString(summaryTemp);
		
		var resumedFromIndex = fields.get(INTERRUPTION_HISTORY_INDEX);
		resumedFrom = resumedFromIndex == null ? null : frames[(int) resumedFromIndex - frameOffset];
		
		var interruptingIndex = fields.get(HISTORY_INDEX_INTERRUPTED_BY_SELF);
		interruptedByThis = interruptingIndex == null ? null : frames[(int) interruptingIndex - frameOffset];
	}
	
	void finishBuilding() {
		if (resumedFrom != null) {
			resumedFrom.getContext().resumedBy = frame;
		}
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return properties.getValueAt(0).get(name);
	}
	
	public TreeItem<X2VersionedDatumTreeItem> getTree(PrimitiveInterner interner) {
		return properties.getTreeNodeAt(interner, null, 0, false);
	}

	@Override
	public int getSizeInFile() {
		return sizeInFile;
	}

	public UnrealName getType() {
		return type;
	}

	public HistoryFrame getFrame() {
		return frame;
	}

	public String getSummary() {
		return summary;
	}

	public EInterruptionStatus getInterruptionStatus() {
		return interruptionStatus;
	}

	public HistoryFrame getResumedFrom() {
		return resumedFrom;
	}

	public HistoryFrame getInterruptedByThis() {
		return interruptedByThis;
	}

	public HistoryFrame getResumedBy() {
		return resumedBy;
	}
	
}
