package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_HeadquartersXCom extends XComGameState_Airship {
	
	public List<ReserveSquad> AllSquads;
	public List<GeneratedMissionData> arrGeneratedMissionData;
	public List<IXComStateObjectReference<XComGameState_Unit>> Crew;
	public List<HQOrder> CurrentOrders;
	public List<Integer> EverAcquiredInventoryCounts;
	public List<UnrealName> EverAcquiredInventoryTypes;
	public List<UnrealName> ExtraUpgradeWeaponCats;
	@UnrealUntypedProperty(1)
	public Map<String, Integer> GenericKVP;
	public List<IXComStateObjectReference<XComGameState_Item>> Inventory;
	public List<PendingFacilityDiscount> PendingFacilityDiscounts;
	public List<AmbientNarrativeInfo> PlayedAmbientNarrativeMoments;
	public List<UnrealName> PlayedAmbientSpeakers;
	public List<AmbientNarrativeInfo> PlayedArmorIntroNarrativeMoments;
	public List<AmbientNarrativeInfo> PlayedEquipItemNarrativeMoments;
	public List<AmbientNarrativeInfo> PlayedLootNarrativeMoments;
	public List<String> PlayedTacticalNarrativeMomentsCurrentMapOnly;
	public List<DynamicPropertySet> QueuedDynamicPopups;
	public List<UnrealName> RequiredSpawnGroups;
	public List<UnrealName> SavedSoldierUnlockTemplates;
	public List<ScanRateMod> ScanRateMods;
	public List<UnrealName> SeenCharacterTemplates;
	public List<UnrealName> SeenClassMovies;
	public List<UnrealName> SoldierClassDeck;
	public List<SoldierClassCount> SoldierClassDistribution;
	public List<UnrealName> SoldierUnlockTemplates;
	public List<IXComStateObjectReference<XComGameState_Unit>> Squad;
	public List<UnrealName> TacticalGameplayTags;
	public List<UnrealName> UnlockedItems;
	
}
