package com.github.rcd47.x2data.lib.unreal.mappings.x2stats;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class X2S_HackingReward {
	
	@UnrealDataTypeHint(UnrealDataType.nameproperty)
	public String RewardTemplateName; // corresponds to a X2HackRewardTemplate
	public int ChanceToSucceed;
	
}
