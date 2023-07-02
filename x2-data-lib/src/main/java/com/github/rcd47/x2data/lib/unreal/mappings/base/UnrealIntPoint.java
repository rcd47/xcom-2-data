package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("IntPoint")
@UnrealUntypedStruct
public class UnrealIntPoint {
	
	@UnrealUntypedProperty(1)
	public int X;
	@UnrealUntypedProperty(2)
	public int Y;
	
}
