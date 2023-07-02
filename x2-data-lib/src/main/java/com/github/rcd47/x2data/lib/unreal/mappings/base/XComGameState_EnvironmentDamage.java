package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

public class XComGameState_EnvironmentDamage extends XComGameState_BaseObject {
	
	public List<TTile> AdjacentFractureTiles;
	public List<ActorIdentifier> DamageActors;
	public List<TTile> DamageTiles;
	public List<ActorIdentifier> DestroyedActors;
	
}
