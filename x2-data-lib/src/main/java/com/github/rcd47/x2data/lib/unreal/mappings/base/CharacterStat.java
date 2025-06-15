package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class CharacterStat {
	
	public float BaseMaxValue;
	public float CurrentValue;
	public float MaxValue;
	public List<Float> StatModAmounts;
	public List<IXComStateObjectReference<XComGameState_Effect>> StatMods;
	
}
