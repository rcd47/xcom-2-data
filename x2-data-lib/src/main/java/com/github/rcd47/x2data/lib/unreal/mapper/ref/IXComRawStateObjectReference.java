package com.github.rcd47.x2data.lib.unreal.mapper.ref;

/**
 * Sometimes, state objects just have an int field to point to another state object instead of using StateObjectReference.
 */
public interface IXComRawStateObjectReference<T> extends IXComObjectReference<T> {
	
	int id();
	
}
