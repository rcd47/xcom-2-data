package com.github.rcd47.x2data.lib.unreal.mapper.ref;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public interface IXComObjectReferenceResolver {
	
	<T> IXComIndexObjectReference<T> createIndexReference(Class<T> referencedObjectType, int objectIndex);
	
	<T> IXComStateObjectReference<T> createStateReference(Class<T> referencedObjectType, int objectId);
	
	<T> IXComRawStateObjectReference<T> createRawStateReference(Class<T> referencedObjectType, int objectId);
	
	<T> IXComNameObjectReference<T> createNameReference(Class<T> referencedObjectType, UnrealName objectName);
	
}
