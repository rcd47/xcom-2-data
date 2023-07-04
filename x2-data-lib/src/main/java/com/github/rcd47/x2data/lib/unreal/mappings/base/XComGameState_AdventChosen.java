package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class XComGameState_AdventChosen extends XComGameState_GeoscapeCharacter {
	
	public IXComNameObjectReference<X2AdventChosenTemplate> m_TemplateName;
	public List<String> MonthActivities;
	public List<UnrealName> RevealedChosenTraits;
	public List<UnrealName> Strengths;
	public List<UnrealName> Weaknesses;
	
}
