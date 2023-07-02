package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_NarrativeManager extends XComGameState_BaseObject {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> Narratives;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PlayedThisGame;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PlayedThisMission;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> PlayedThisTurn;
	
}
