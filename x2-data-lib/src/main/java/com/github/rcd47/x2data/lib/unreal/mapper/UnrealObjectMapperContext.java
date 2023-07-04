package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComObjectReferenceResolver;

class UnrealObjectMapperContext {
	
	final Deque<IUnrealFieldMapper> mapperStack;
	final IXComObjectReferenceResolver referenceResolver;
	
	UnrealObjectMapperContext(Deque<IUnrealFieldMapper> mapperStack,
			IXComObjectReferenceResolver referenceResolver) {
		this.mapperStack = mapperStack;
		this.referenceResolver = referenceResolver;
	}
	
}
