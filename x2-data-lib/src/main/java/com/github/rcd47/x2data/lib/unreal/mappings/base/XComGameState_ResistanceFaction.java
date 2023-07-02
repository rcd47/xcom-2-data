package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_ResistanceFaction extends XComGameState_GeoscapeCharacter {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> AvailableCovertActions;
	public List<StateObjectReference> CardSlots;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CompletedCovertActions;
	public List<StateObjectReference> CovertActions;
	public List<StateObjectReference> GoldenPathActions;
	public List<StateObjectReference> NewPlayableCards;
	public List<StateObjectReference> OldCardSlots;
	public List<StateObjectReference> PlayableCards;
	public List<StateObjectReference> TerritoryRegions;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> Traits;
	
}
