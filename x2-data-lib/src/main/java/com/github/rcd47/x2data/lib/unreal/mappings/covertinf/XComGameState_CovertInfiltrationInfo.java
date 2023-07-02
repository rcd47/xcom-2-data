package com.github.rcd47.x2data.lib.unreal.mappings.covertinf;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;
import com.github.rcd47.x2data.lib.unreal.mappings.base.StateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_BaseObject;

public class XComGameState_CovertInfiltrationInfo extends XComGameState_BaseObject {
	
	public List<CharacterGroupKillCount> CharacterGroupsKillTracker;
	public List<StateObjectReference> CovertActionsToRemove;
	public List<StateObjectReference> MissionsToShowAlertOnStrategyMap;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> TutorialStagesShown;
	public List<StateObjectReference> UnitsStartedMissionBelowReadyWill;
	public List<StateObjectReference> UnitsToConsiderUpgradingGearOnMissionExit;
	
}
