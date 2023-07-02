package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

abstract class UnrealPrimitiveMapperBase implements IUnrealFieldMapper {
	
	private Deque<IUnrealFieldMapper> mapperStack;
	
	protected UnrealPrimitiveMapperBase(Deque<IUnrealFieldMapper> mapperStack) {
		this.mapperStack = mapperStack;
	}

	protected void visitValue(Object value) {
		mapperStack.pop();
		mapperStack.peek().up(value);
	}
	
}
