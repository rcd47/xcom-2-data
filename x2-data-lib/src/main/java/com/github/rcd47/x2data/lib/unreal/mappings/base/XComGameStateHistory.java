package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComIndexObjectReference;

public class XComGameStateHistory {
	
	public int CurrRandomSeed;
	public List<IXComIndexObjectReference<XComGameState>> History;
	public int NumArchivedFrames;
	
}
