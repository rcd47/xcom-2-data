package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_HeadquartersAlien extends XComGameState_BaseObject {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> Actions;
	public List<StateObjectReference> ActiveDarkEvents;
	public List<StateObjectReference> AdventChosen;
	public List<StateObjectReference> CapturedSoldiers;
	public List<StateObjectReference> ChosenDarkEvents;
	public List<StrategyCostScalar> CostScalars;
	public List<DoomGenData> FacilityDoomData;
	public List<PendingDoom> PendingDoomData;
	
}
