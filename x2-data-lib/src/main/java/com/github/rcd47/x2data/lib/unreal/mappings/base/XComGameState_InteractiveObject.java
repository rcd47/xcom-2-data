package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_InteractiveObject extends XComGameState_Destructible {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> HackRewards;
	public List<Integer> HackRollMods;
	
}
