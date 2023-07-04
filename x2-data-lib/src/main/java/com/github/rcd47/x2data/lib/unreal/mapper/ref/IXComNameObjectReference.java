package com.github.rcd47.x2data.lib.unreal.mapper.ref;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public interface IXComNameObjectReference<T> extends IXComObjectReference<T> {
	
	UnrealName name();
	
}
