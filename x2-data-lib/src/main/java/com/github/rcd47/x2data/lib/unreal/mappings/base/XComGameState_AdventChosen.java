package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_AdventChosen extends XComGameState_GeoscapeCharacter {
	
	public List<StateObjectReference> CapturedSoldiers;
	public List<StateObjectReference> ControlledContinents;
	public String m_TemplateName;
	public List<String> MonthActivities;
	public List<StateObjectReference> PreviousMonthActions;
	public List<StateObjectReference> RegionAttackDeck;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> RevealedChosenTraits;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> Strengths;
	public List<StateObjectReference> TerritoryRegions;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> Weaknesses;
	
}
