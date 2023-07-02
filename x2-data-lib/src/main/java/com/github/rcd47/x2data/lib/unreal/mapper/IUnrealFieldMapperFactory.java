package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

interface IUnrealFieldMapperFactory {
	
	IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue);
	
	default Object createDefaultValue() {
		return null;
	}
	
}
