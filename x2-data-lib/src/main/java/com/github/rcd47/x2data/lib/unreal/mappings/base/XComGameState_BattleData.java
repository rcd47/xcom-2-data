package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComRawStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class XComGameState_BattleData extends XComGameState_BaseObject {
	
	public List<UnrealName> ActiveSitReps;
	public List<UnrealName> AlertEventIDs;
	public List<UnrealName> AllowedAbilities;
	public List<UnrealName> AutoLootBucket;
	public List<UnrealName> CarriedOutLootBucket;
	public DirectTransferInformation DirectTransferInfo;
	public List<UnrealName> DisallowedAbilities;
	public List<UnrealName> HighlightedObjectiveAbilities;
	public TDateTime LocalTime;
	public List<UnrealName> LostSwarmIDs;
	public List<Integer> m_arrSecondWave;
	public IXComRawStateObjectReference<XComGameState_MissionSite> m_iMissionID;
	public String m_strDesc;
	public String m_strLocation;
	public String m_strOpName;
	public List<Integer> MaxLostSpawnTurnCooldown;
	public List<Integer> MinLostSpawnTurnCooldown;
	// PlayerTurnOrder items can be XCGS_Player or XCGS_AIGroup
	public List<IXComStateObjectReference<XComGameState_BaseObject>> PlayerTurnOrder;
	public List<UnrealName> PopularSupportEventIDs;
	public List<UnrealName> StrategyHackRewards;
	public List<Integer> TacticalEventGameStates;
	public List<UnrealName> TacticalHackRewards;
	public List<UnrealName> UniqueHackRewardsAcquired;
	
}
