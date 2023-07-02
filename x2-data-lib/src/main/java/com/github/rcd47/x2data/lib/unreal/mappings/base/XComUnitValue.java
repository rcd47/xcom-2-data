package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;

@UnrealTypeName("UnitValue")
@UnrealUntypedStruct
public class XComUnitValue {
	
	@UnrealUntypedProperty(2)
	public EUnitValueCleanup eCleanup;
	@UnrealUntypedProperty(1)
	public float fValue;
	
}
