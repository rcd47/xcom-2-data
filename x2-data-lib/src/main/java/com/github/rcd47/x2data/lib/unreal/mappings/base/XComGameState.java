package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComIndexObjectReference;

public class XComGameState {
	
	public List<IXComIndexObjectReference<XComGameState_BaseObject>> GameStates;
	public int HistoryIndex;
	public IXComIndexObjectReference<XComGameStateContext> StateChangeContext;
	public String TimeStamp;
	
}
