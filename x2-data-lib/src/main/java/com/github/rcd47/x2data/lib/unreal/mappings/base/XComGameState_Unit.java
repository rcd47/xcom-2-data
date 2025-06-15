package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_Unit extends XComGameState_BaseObject {
	
	public List<IXComStateObjectReference<XComGameState_Ability>> Abilities;
	public List<SoldierRankAbilities> AbilityTree;
	public List<UnrealName> AcquiredTraits;
	public List<UnrealName> ActionPoints;
	public List<UnrealName> AffectedByEffectNames;
	public List<IXComStateObjectReference<XComGameState_Effect>> AffectedByEffects;
	public List<UnrealName> AlertTraits;
	public List<SoldierBond> AllSoldierBonds;
	public List<AppearanceInfo> AppearanceStore;
	public List<UnrealName> AppliedEffectNames;
	public List<IXComStateObjectReference<XComGameState_Effect>> AppliedEffects;
	public List<ClassAgnosticAbility> AWCAbilities;
	public boolean bIsSpecial;
	public Map<ECharStatType, CharacterStat> CharacterStats;
	public IXComStateObjectReference<XComGameState_AdventChosen> ChosenRef;
	public ECombatIntelligence ComInt;
	public IXComStateObjectReference<XComGameState_Player> ControllingPlayer;
	public List<UnrealName> CuredTraits;
	public List<UnrealName> CurrentHackRewards;
	public List<DamageResult> DamageResults;
	public List<Integer> EnemiesInteractedWithSinceLastTurn;
	public List<UnrealName> HackRewards;
	public List<Integer> HackRollMods;
	public List<IXComStateObjectReference<XComGameState_Item>> InventoryItems;
	public List<UnrealName> KilledByDamageTypes;
	public IXComNameObjectReference<X2SoldierClassTemplate> m_SoldierClassTemplateName;
	public List<SCATProgression> m_SoldierProgressionAbilties;
	public int m_SoldierRank;
	public String m_strEpitaph;
	public IXComNameObjectReference<X2CharacterTemplate> m_TemplateName;
	public EMentalState MentalState = EMentalState.eMentalState_Ready; // set in defaultproperties
	public List<NegativeTraitRecoveryInfo> NegativeTraits;
	public UnrealName nmCountry;
	public List<EquipmentInfo> OldInventoryItems;
	public List<UnrealName> PendingTraits;
	public List<SCATProgression> PsiAbilities;
	public List<UnrealName> ReserveActionPoints;
	public List<UnrealName> SkippedActionPoints;
	public List<SquadmateScore> SoldierRelationships;
	public String strBackground;
	public String strFirstName;
	public String strLastName;
	public String strNickName;
	public TTile TileLocation;
	public List<TraversalChange> TraversalChanges;
	@UnrealUntypedProperty(1)
	public Map<UnrealName, XComUnitValue> UnitValues;
	public List<UnrealName> WillEventsActivatedThisMission;
	public List<UnrealName> WorldMessageTraits;
	
}
