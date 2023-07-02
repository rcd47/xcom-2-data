package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class EffectResults {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> ApplyResults;
	public List<Integer> Effects;
	public List<X2EffectTemplateRef> TemplateRefs;
	
}
