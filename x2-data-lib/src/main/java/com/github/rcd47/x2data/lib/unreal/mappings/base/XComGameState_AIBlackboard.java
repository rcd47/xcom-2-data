package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;

public class XComGameState_AIBlackboard extends XComGameState_BaseObject {
	
	@UnrealUntypedProperty(1)
	public Map<String, Integer> IntKVP;
	
}
