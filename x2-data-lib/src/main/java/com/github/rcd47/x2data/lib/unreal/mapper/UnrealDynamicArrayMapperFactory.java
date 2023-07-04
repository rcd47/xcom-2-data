package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;

class UnrealDynamicArrayMapperFactory implements IUnrealFieldMapperFactory {

	private IUnrealFieldMapperFactory elementMapperFactory;
	
	UnrealDynamicArrayMapperFactory(IUnrealFieldMapperFactory elementMapperFactory) {
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealDynamicArrayMapper(context, elementMapperFactory);
	}

	@Override
	public Object createDefaultValue() {
		return new ArrayList<>();
	}

}
