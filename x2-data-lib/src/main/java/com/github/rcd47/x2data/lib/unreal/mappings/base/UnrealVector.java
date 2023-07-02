package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("Vector")
@UnrealUntypedStruct
public class UnrealVector {
	
	@UnrealUntypedProperty(1)
	public float X;
	@UnrealUntypedProperty(2)
	public float Y;
	@UnrealUntypedProperty(3)
	public float Z;
	
}
