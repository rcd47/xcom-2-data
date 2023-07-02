package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class DamageResult {
	
	public boolean bFreeKill;
	public int Context;
	public int DamageAmount;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> DamageTypes;
	public int MitigationAmount;
	public int ShieldHP;
	public int Shred;
	public List<DamageModifierInfo> SpecialDamageFactors;
	
}
