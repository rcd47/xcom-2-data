package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class AbilityInputContext {
	
	public IXComNameObjectReference<X2AbilityTemplate> AbilityTemplateName;
	public IXComStateObjectReference<XComGameState_Ability> AbilityRef;
	public IXComStateObjectReference<XComGameState_BaseObject> PrimaryTarget;
	public IXComStateObjectReference<XComGameState_Unit> SourceObject;
	public IXComStateObjectReference<XComGameState_Item> ItemObject;
	public List<PathingInputData> MovementPaths;
	public List<IXComStateObjectReference<XComGameState_BaseObject>> MultiTargets;
	public List<Boolean> MultiTargetsNotified;
	public List<ProjectileTouchEvent> ProjectileEvents;
	public List<UnrealVector> TargetLocations;
	public List<TTile> VisibleNeighborTiles;
	public List<TTile> VisibleTargetedTiles;
	
}
