package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.XComSingletonStateType;

@XComSingletonStateType
public class XComGameState_Analytics extends XComGameState_BaseObject {
	
	@UnrealUntypedProperty(1)
	public Map<String, Double> AnalyticMap;
	@UnrealUntypedProperty(2)
	public Map<String, Double> TacticalAnalyticMap;
	@UnrealUntypedProperty(3)
	public List<Integer> TacticalAnalyticUnits;
	
}
