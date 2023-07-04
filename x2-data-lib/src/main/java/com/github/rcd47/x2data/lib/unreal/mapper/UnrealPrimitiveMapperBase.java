package com.github.rcd47.x2data.lib.unreal.mapper;

abstract class UnrealPrimitiveMapperBase implements IUnrealFieldMapper {
	
	protected final UnrealObjectMapperContext context;
	
	protected UnrealPrimitiveMapperBase(UnrealObjectMapperContext context) {
		this.context = context;
	}

	protected void visitValue(Object value) {
		context.mapperStack.pop();
		context.mapperStack.peek().up(value);
	}
	
}
