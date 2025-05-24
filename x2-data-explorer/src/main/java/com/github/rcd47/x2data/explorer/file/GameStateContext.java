package com.github.rcd47.x2data.explorer.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.EInterruptionStatus;
import com.google.common.base.Throwables;

import groovy.lang.Script;

public class GameStateContext {
	
	private static final UnrealName INTERRUPTION_HISTORY_INDEX = new UnrealName("InterruptionHistoryIndex");
	private static final UnrealName HISTORY_INDEX_INTERRUPTED_BY_SELF = new UnrealName("HistoryIndexInterruptedBySelf");
	private static final UnrealName INTERRUPTION_STATUS = new UnrealName("InterruptionStatus");
	
	private final UnrealName type;
	private final Map<UnrealName, NonVersionedField> fields;
	private final HistoryFrame frame;
	private final String summary;
	private final EInterruptionStatus interruptionStatus;
	private final HistoryFrame resumedFrom;
	private final HistoryFrame interruptedByThis;
	private HistoryFrame resumedBy;
	
	public GameStateContext(GenericObject object, HistoryFrame frame, Map<Integer, HistoryFrame> frames, Script summarizer,
			List<HistoryFileProblem> problemsDetected) {
		this.frame = frame;
		
		type = object.type;
		
		fields = new HashMap<>();
		object.properties.forEach((k, v) -> fields.put(k, new NonVersionedField(v)));
		
		interruptionStatus = Optional
				.ofNullable(fields.get(INTERRUPTION_STATUS))
				.map(s -> EInterruptionStatus.valueOf(((UnrealName) s.getValue()).getOriginal()))
				.orElse(EInterruptionStatus.eInterruptionStatus_None);
		
		String summaryTemp;
		try {
			summarizer.getBinding().setProperty("ctx", this);
			summaryTemp = (String) summarizer.run();
		} catch (Exception e) {
			summaryTemp = "";
			problemsDetected.add(new HistoryFileProblem(
					frame, this, null, "Summary script failed. Stack trace:\n" + Throwables.getStackTraceAsString(e)));
		}
		summary = summaryTemp;
		
		var resumedFromIndex = fields.get(INTERRUPTION_HISTORY_INDEX);
		if (resumedFromIndex == null) {
			resumedFrom = null;
		} else {
			resumedFrom = frames.get(resumedFromIndex.getValue());
			resumedFrom.getContext().resumedBy = frame;
		}
		
		var interruptingIndex = fields.get(HISTORY_INDEX_INTERRUPTED_BY_SELF);
		interruptedByThis = interruptingIndex == null ? null : frames.get(interruptingIndex.getValue());
	}
	
	// Groovy support
	public Object propertyMissing(String name) {
		return fields.get(new UnrealName(name));
	}

	public UnrealName getType() {
		return type;
	}

	public Map<UnrealName, NonVersionedField> getFields() {
		return fields;
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
