package com.github.rcd47.x2data.lib.unreal.mappings.mocx;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.base.SoldierClassAbilityType;
import com.github.rcd47.x2data.lib.unreal.mappings.base.StateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_BaseObject;

public class XComGameState_Unit_DarkXComInfo extends XComGameState_BaseObject {
	
	public List<SoldierClassAbilityType> AWCAbilities;
	public List<StateObjectReference> KilledXCOMUnits;
	public List<SoldierClassAbilityType> SoldierAbilities;
	
}
