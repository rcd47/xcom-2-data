package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComIndexObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class DamageResult {
	
	public boolean bFreeKill;
	public IXComIndexObjectReference<XComGameStateContext> Context;
	public int DamageAmount;
	public List<UnrealName> DamageTypes;
	public int MitigationAmount;
	public int ShieldHP;
	public int Shred;
	public List<DamageModifierInfo> SpecialDamageFactors;
	
}
