package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

public class XComGameState_AIGroup extends XComGameState_BaseObject {
	
	public List<StateObjectReference> m_arrDisplaced;
	public List<TTile> m_arrGuardLocation;
	public List<StateObjectReference> m_arrMembers;
	public List<Integer> PreviouslyConcealedUnitObjectIDs;
	public List<Integer> SurprisedScamperUnitIDs;
	public List<Integer> WaitingOnUnitList;
	
}
