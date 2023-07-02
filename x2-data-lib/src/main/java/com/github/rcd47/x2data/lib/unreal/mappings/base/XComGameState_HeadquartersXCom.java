package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_HeadquartersXCom extends XComGameState_Airship {
	
	public List<ReserveSquad> AllSquads;
	public List<GeneratedMissionData> arrGeneratedMissionData;
	public List<StateObjectReference> Clerks;
	public List<StateObjectReference> Crew;
	public List<HQOrder> CurrentOrders;
	public List<StateObjectReference> DeadCrew;
	public List<Integer> EverAcquiredInventoryCounts;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> EverAcquiredInventoryTypes;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ExtraUpgradeWeaponCats;
	public List<StateObjectReference> Facilities;
	@UnrealUntypedProperty(1)
	public Map<String, Integer> GenericKVP;
	public List<StateObjectReference> IgnoredBreakthroughTechs;
	public List<StateObjectReference> Inventory;
	public List<StateObjectReference> LootRecovered;
	public List<StateObjectReference> MalfunctionFacilities;
	public List<StateObjectReference> NewStaffRefs;
	public List<PendingFacilityDiscount> PendingFacilityDiscounts;
	public List<AmbientNarrativeInfo> PlayedAmbientNarrativeMoments;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PlayedAmbientSpeakers;
	public List<AmbientNarrativeInfo> PlayedArmorIntroNarrativeMoments;
	public List<AmbientNarrativeInfo> PlayedEquipItemNarrativeMoments;
	public List<AmbientNarrativeInfo> PlayedLootNarrativeMoments;
	public List<String> PlayedTacticalNarrativeMomentsCurrentMapOnly;
	public List<StateObjectReference> Projects;
	public List<DynamicPropertySet> QueuedDynamicPopups;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> RequiredSpawnGroups;
	public List<StateObjectReference> Rooms;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SavedSoldierUnlockTemplates;
	public List<ScanRateMod> ScanRateMods;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SeenCharacterTemplates;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SeenClassMovies;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SoldierClassDeck;
	public List<SoldierClassCount> SoldierClassDistribution;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SoldierUnlockTemplates;
	public List<StateObjectReference> Squad;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> TacticalGameplayTags;
	public List<StateObjectReference> TacticalTechBreakthroughs;
	public List<StateObjectReference> TechsResearched;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> UnlockedItems;
	
}
