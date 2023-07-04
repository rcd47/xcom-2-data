package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class MissionDefinition {
	
	public List<String> ExcludedParcelObjectiveTags;
	public List<String> ExcludedPlotObjectiveTags;
	public List<UnrealName> ForcedSitreps;
	public List<UnrealName> ForcedTacticalTags;
	public List<String> MapNames;
	public List<MissionObjectiveDefinition> MissionObjectives;
	public List<UnrealName> MissionSchedules;
	public List<String> RequiredMissionItem;
	public List<String> RequiredParcelObjectiveTags;
	public List<String> RequiredPlotObjectiveTags;
	public List<UnrealName> SpecialSoldiers;
	public List<Integer> SquadSizeMin;
	
}
