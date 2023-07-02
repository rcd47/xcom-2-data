package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.util.List;

public class AbilityResultContext {
	
	public List<EffectRedirect> EffectRedirects;
	public EAbilityHitResult HitResult;
	public List<ArmorMitigationResults> MultiTargetArmorMitigation;
	public List<EffectResults> MultiTargetEffectResults;
	public List<OverriddenEffectsByType> MultiTargetEffectsOverrides;
	public List<EAbilityHitResult> MultiTargetHitResults;
	public List<Integer> MultiTargetStatContestResult;
	public List<PathingResultData> PathResults;
	public List<UnrealVector> ProjectileHitLocations;
	public List<TTile> RelevantEffectTiles;
	public EffectResults ShooterEffectResults;
	public int StatContestResult;
	public EffectResults TargetEffectResults;
	
}
