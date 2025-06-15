package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_Analytics extends XComGameState_BaseObject {
	
	@UnrealUntypedProperty(1)
	public Map<String, Double> AnalyticMap; // TODO before WOTC, key was UnrealName instead of String
	@UnrealUntypedProperty(2)
	public Map<String, Double> TacticalAnalyticMap; // TODO before WOTC, key was UnrealName instead of String
	@UnrealUntypedProperty(3)
	public List<Integer> TacticalAnalyticUnits;
	
}
