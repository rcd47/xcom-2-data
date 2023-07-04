package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class XComGameState_MissionCalendar extends XComGameState_BaseObject {
	
	public List<UnrealName> CreatedMissionSources;
	public List<MissionCalendarDate> CurrentMissionMonth;
	public List<RandomMissionDeck> CurrentRandomMissionDecks;
	public List<UnrealName> MissionPopupSources;
	public List<MissionRewardDeck> MissionRewardDecks;
	public List<MissionRewardDeck> MissionRewardExcludeDecks;
	
}
