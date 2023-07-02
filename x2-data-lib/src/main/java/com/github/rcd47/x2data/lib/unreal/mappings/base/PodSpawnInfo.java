package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class PodSpawnInfo {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SelectedCharacterTemplateNames;
	
}