package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;
import java.util.List;

class UnrealStaticArrayListMapperFactory implements IUnrealFieldMapperFactory {

	private IUnrealFieldMapperFactory elementMapperFactory;
	private int size;
	
	UnrealStaticArrayListMapperFactory(IUnrealFieldMapperFactory elementMapperFactory, int size) {
		this.elementMapperFactory = elementMapperFactory;
		this.size = size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealStaticArrayListMapper((List<Object>) currentValue, context, elementMapperFactory);
	}

	@Override
	public Object createDefaultValue() {
		var list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(elementMapperFactory.createDefaultValue());
		}
		return list;
	}

}
