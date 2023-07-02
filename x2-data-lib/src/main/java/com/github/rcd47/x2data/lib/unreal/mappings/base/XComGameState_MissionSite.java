package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_MissionSite extends XComGameState_GeoscapeEntity {
	
	public List<String> AdditionalRequiredPlotObjectiveTags;
	public List<String> ExcludeMissionFamilies;
	public List<String> ExcludeMissionTypes;
	public List<StrategyCostReward> IntelOptions;
	public List<StrategyCostReward> PurchasedIntelOptions;
	public List<StateObjectReference> Rewards;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> TacticalGameplayTags;
	
}
