package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("TwoVectors")
@UnrealUntypedStruct
public class UnrealTwoVectors {
	
	@UnrealUntypedProperty(1)
	public UnrealVector v1;
	@UnrealUntypedProperty(2)
	public UnrealVector v2;
	
}
