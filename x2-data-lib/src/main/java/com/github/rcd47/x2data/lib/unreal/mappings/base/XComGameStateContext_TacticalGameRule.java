package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;

public class XComGameStateContext_TacticalGameRule extends XComGameStateContext {
	
	public GameRuleStateChange GameRuleType;
	public IXComStateObjectReference<XComGameState_Player> PlayerRef;
	
}
