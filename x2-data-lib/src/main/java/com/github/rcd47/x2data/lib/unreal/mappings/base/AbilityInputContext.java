package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class AbilityInputContext {
	
	@UnrealDataTypeHint(UnrealDataType.nameproperty)
	public String AbilityTemplateName;
	public StateObjectReference AbilityRef;
	public StateObjectReference PrimaryTarget;
	public StateObjectReference SourceObject;
	public StateObjectReference ItemObject;
	public List<PathingInputData> MovementPaths;
	public List<StateObjectReference> MultiTargets;
	public List<Boolean> MultiTargetsNotified;
	public List<ProjectileTouchEvent> ProjectileEvents;
	public List<UnrealVector> TargetLocations;
	public List<TTile> VisibleNeighborTiles;
	public List<TTile> VisibleTargetedTiles;
	
}
