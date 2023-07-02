package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_Item extends XComGameState_BaseObject {
	
	public List<StateObjectReference> ContainedItems;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> m_arrWeaponUpgradeNames;
	public List<@UnrealDataTypeHint(UnrealDataType.objectproperty) Integer> m_arrWeaponUpgradeTemplates;
	@UnrealDataTypeHint(UnrealDataType.nameproperty)
	public String m_TemplateName;
	public String Nickname;
	public List<StatBoost> StatBoosts;
	
}
