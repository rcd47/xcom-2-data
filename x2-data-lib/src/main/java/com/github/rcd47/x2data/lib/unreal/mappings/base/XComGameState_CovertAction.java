package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;

public class XComGameState_CovertAction extends XComGameState_GeoscapeEntity {
	
	public List<CovertActionCostSlot> CostSlots;
	public List<@UnrealDataTypeHint(UnrealDataType.nameproperty) String> NegatedRisks;
	public List<StateObjectReference> RewardRefs;
	public List<CovertActionRisk> Risks;
	public List<CovertActionStaffSlot> StaffSlots;
	
}
