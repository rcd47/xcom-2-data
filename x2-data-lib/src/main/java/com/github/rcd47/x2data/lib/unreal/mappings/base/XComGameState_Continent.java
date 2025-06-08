package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class XComGameState_Continent extends XComGameState_GeoscapeEntity {
	
	public List<IXComStateObjectReference<XComGameState_WorldRegion>> Regions;
	public IXComNameObjectReference<X2ContinentTemplate> m_TemplateName;
	
}
