package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class MissionDefinition {
	
	public List<String> ExcludedParcelObjectiveTags;
	public List<String> ExcludedPlotObjectiveTags;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ForcedSitreps;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ForcedTacticalTags;
	public List<String> MapNames;
	public List<MissionObjectiveDefinition> MissionObjectives;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> MissionSchedules;
	public List<String> RequiredMissionItem;
	public List<String> RequiredParcelObjectiveTags;
	public List<String> RequiredPlotObjectiveTags;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SpecialSoldiers;
	public List<Integer> SquadSizeMin;
	
}
