package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_BattleData extends XComGameState_BaseObject {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ActiveSitReps;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AlertEventIDs;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AllowedAbilities;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AutoLootBucket;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CarriedOutLootBucket;
	public DirectTransferInformation DirectTransferInfo;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> DisallowedAbilities;
	public List<StateObjectReference> FactionHeroesCaptured;
	public List<StateObjectReference> FactionHeroesHighLevel;
	public List<StateObjectReference> FactionHeroesKilled;
	public List<StateObjectReference> FactionHeroesOnMission;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> HighlightedObjectiveAbilities;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> LostSwarmIDs;
	public List<Integer> m_arrSecondWave;
	public String m_strOpName;
	public List<Integer> MaxLostSpawnTurnCooldown;
	public List<Integer> MinLostSpawnTurnCooldown;
	public List<StateObjectReference> PlayerTurnOrder;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PopularSupportEventIDs;
	public List<StateObjectReference> RewardUnitOriginals;
	public List<StateObjectReference> RewardUnits;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> StrategyHackRewards;
	public List<Integer> TacticalEventGameStates;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> TacticalHackRewards;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> UniqueHackRewardsAcquired;
	
}
