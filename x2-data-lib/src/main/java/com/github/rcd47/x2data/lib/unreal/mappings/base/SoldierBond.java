package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class SoldierBond {
	
	public int BondLevel;
	public IXComStateObjectReference<XComGameState_Unit> Bondmate;
	public int Cohesion;
	public float Compatibility;
	
}
