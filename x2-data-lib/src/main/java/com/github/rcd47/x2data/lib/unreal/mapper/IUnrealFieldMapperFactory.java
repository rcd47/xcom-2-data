package com.github.rcd47.x2data.lib.unreal.mapper;

interface IUnrealFieldMapperFactory {
	
	IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue);
	
	default Object createDefaultValue() {
		return null;
	}
	
}
