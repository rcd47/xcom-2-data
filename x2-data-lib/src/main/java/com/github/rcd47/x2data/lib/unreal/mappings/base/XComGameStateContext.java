package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameStateContext {
	
	public EInterruptionStatus InterruptionStatus;
	public int InterruptionHistoryIndex; // If this game state is resuming from an interruption, this index points back to the game state that are resuming from
	public int ResumeHistoryIndex; // If this game state has been interrupted, this index points forward to the game state that will resume
	public List<@UnrealDataTypeHint(UnrealDataType.delegateproperty) Integer> PostBuildVisualizationFn;
	public List<@UnrealDataTypeHint(UnrealDataType.delegateproperty) Integer> PreBuildVisualizationFn;
	
}
