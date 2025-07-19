package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class PathingInputData {
	
	public List<Integer> CostIncreases;
	public List<Integer> Destructibles;
	public List<PathPoint> MovementData;
	public List<TTile> MovementTiles;
	public IXComStateObjectReference<XComGameState_Unit> MovingUnitRef;
	public List<TTile> WaypointTiles;
	
}
