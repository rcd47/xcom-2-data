package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class XComGameState_Item extends XComGameState_BaseObject {
	
	public List<UnrealName> m_arrWeaponUpgradeNames;
	public IXComNameObjectReference<X2ItemTemplate> m_TemplateName;
	public String Nickname;
	public List<StatBoost> StatBoosts;
	
}
