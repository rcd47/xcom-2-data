package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("Vector2D")
@UnrealUntypedStruct
public class UnrealVector2D {
	
	@UnrealUntypedProperty(1)
	public float X;
	@UnrealUntypedProperty(2)
	public float Y;
	
}
