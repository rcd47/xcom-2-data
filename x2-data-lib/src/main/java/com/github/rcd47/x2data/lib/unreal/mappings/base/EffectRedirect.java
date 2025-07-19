package com.github.rcd47.x2data.lib.unreal.mappings.base;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class EffectRedirect {
	
	// the target refs can definitely be Units
	// theoretically, I think they could be other things too, like an InteractiveObject
	public IXComStateObjectReference<XComGameState_BaseObject> OriginalTargetRef;
	public IXComStateObjectReference<XComGameState_BaseObject> RedirectedToTargetRef;
	public UnrealName RedirectReason;
	public EffectResults RedirectResults;
	
}
