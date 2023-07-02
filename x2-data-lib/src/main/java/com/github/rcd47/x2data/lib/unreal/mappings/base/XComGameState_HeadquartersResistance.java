package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_HeadquartersResistance extends XComGameState_BaseObject {
	
	public List<StateObjectReference> ActivePOIs;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CovertActionDarkEventRisks;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CovertActionExclusionList;
	public List<StateObjectReference> Factions;
	public List<StateObjectReference> OldWildCardSlots;
	public List<StateObjectReference> PersonnelGoods;
	public List<String> RecapGlobalEffectsBad;
	public List<String> RecapGlobalEffectsGood;
	public List<String> RecapRewardsStrings;
	public List<StateObjectReference> Recruits;
	public List<TResistanceActivity> ResistanceActivities;
	public List<Commodity> ResistanceGoods;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> RookieCovertActions;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SoldierClassDeck;
	public List<StateObjectReference> WildCardSlots;
	
}
