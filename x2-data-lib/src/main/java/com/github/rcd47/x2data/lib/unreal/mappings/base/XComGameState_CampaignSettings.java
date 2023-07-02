package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_CampaignSettings extends XComGameState_BaseObject {
	
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> EnabledOptionalNarrativeDLC;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> RequiredDLC;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> SecondWaveOptions;
	
}
