package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_Unit extends XComGameState_BaseObject {
	
	public List<StateObjectReference> Abilities;
	public List<SoldierRankAbilities> AbilityTree;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AcquiredTraits;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ActionPoints;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AffectedByEffectNames;
	public List<StateObjectReference> AffectedByEffects;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AlertTraits;
	public List<SoldierBond> AllSoldierBonds;
	public List<AppearanceInfo> AppearanceStore;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AppliedEffectNames;
	public List<StateObjectReference> AppliedEffects;
	public List<ClassAgnosticAbility> AWCAbilities;
	public List<CharacterStat> CharacterStats;
	public StateObjectReference ChosenRef;
	public ECombatIntelligence ComInt;
	public StateObjectReference ControllingPlayer;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CuredTraits;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CurrentHackRewards;
	public List<DamageResult> DamageResults;
	public List<Integer> EnemiesInteractedWithSinceLastTurn;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> HackRewards;
	public List<Integer> HackRollMods;
	public List<StateObjectReference> InventoryItems;
	public List<StateObjectReference> KillAssists;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> KilledByDamageTypes;
	public List<StateObjectReference> KilledUnits;
	@UnrealDataTypeHint(UnrealDataType.nameproperty)
	public String m_SoldierClassTemplateName;
	public List<SCATProgression> m_SoldierProgressionAbilties;
	public int m_SoldierRank;
	public String m_strEpitaph;
	@UnrealDataTypeHint(UnrealDataType.nameproperty)
	public String m_TemplateName;
	public EMentalState MentalState = EMentalState.eMentalState_Ready; // set in defaultproperties
	public List<StateObjectReference> MPBaseLoadoutItems;
	public List<NegativeTraitRecoveryInfo> NegativeTraits;
	public String nmCountry;
	public List<EquipmentInfo> OldInventoryItems;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PendingTraits;
	public List<SCATProgression> PsiAbilities;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ReserveActionPoints;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SkippedActionPoints;
	public List<SquadmateScore> SoldierRelationships;
	public String strBackground;
	public String strFirstName;
	public String strLastName;
	public String strNickName;
	public TTile TileLocation;
	public List<TraversalChange> TraversalChanges;
	@UnrealUntypedProperty(1)
	public Map<@UnrealDataTypeHint(UnrealDataType.nameproperty) String, XComUnitValue> UnitValues;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> WillEventsActivatedThisMission;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> WorldMessageTraits;
	
}
