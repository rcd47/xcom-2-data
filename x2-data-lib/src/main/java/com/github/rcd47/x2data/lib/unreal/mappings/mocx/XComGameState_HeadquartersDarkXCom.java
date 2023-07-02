package com.github.rcd47.x2data.lib.unreal.mappings.mocx;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;
import com.github.rcd47.x2data.lib.unreal.mappings.base.SoldierClassCount;
import com.github.rcd47.x2data.lib.unreal.mappings.base.StateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_BaseObject;

public class XComGameState_HeadquartersDarkXCom extends XComGameState_BaseObject {
	
	public List<StateObjectReference> Crew;
	public List<StateObjectReference> DeadCrew;
	public List<StateObjectReference> LastMission_Squad;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SoldierClassDeck;
	public List<SoldierClassCount> SoldierClassDistribution;
	public List<StateObjectReference> Squad;
	
}
