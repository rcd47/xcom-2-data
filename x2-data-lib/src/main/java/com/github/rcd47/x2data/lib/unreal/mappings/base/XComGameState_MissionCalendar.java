package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_MissionCalendar extends XComGameState_BaseObject {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> CreatedMissionSources;
	public List<MissionCalendarDate> CurrentMissionMonth;
	public List<RandomMissionDeck> CurrentRandomMissionDecks;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> MissionPopupSources;
	public List<MissionRewardDeck> MissionRewardDecks;
	public List<MissionRewardDeck> MissionRewardExcludeDecks;
	
}
