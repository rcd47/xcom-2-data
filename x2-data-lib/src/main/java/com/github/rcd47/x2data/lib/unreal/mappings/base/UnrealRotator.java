package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("Rotator")
@UnrealUntypedStruct
public class UnrealRotator {
	
	@UnrealUntypedProperty(1)
	public int Pitch;
	@UnrealUntypedProperty(3)
	public int Roll;
	@UnrealUntypedProperty(2)
	public int Yaw;
	
}
