package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameStateContext {
	
	public EInterruptionStatus InterruptionStatus;
	public List<@UnrealDataTypeHint(UnrealDataType.delegateproperty) Integer> PostBuildVisualizationFn;
	public List<@UnrealDataTypeHint(UnrealDataType.delegateproperty) Integer> PreBuildVisualizationFn;
	
}
