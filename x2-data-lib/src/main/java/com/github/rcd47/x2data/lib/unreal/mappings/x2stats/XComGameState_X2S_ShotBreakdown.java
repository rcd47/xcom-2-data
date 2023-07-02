package com.github.rcd47.x2data.lib.unreal.mappings.x2stats;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.base.ShotBreakdown;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_BaseObject;

public class XComGameState_X2S_ShotBreakdown extends XComGameState_BaseObject {
	
	public boolean IsHacking;
	public List<X2S_HackingReward> HackingRewards;
	public int SelectedHackingReward;
	public List<ShotBreakdown> MultiShotBreakdowns;
	public ShotBreakdown PrimaryShotBreakdown;
	
}
