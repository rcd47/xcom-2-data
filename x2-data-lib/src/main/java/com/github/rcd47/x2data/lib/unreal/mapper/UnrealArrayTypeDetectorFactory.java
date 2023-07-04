package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;
import java.util.List;

class UnrealArrayTypeDetectorFactory implements IUnrealFieldMapperFactory {

	private IUnrealFieldMapperFactory elementMapperFactory;
	
	UnrealArrayTypeDetectorFactory(IUnrealFieldMapperFactory elementMapperFactory) {
		this.elementMapperFactory = elementMapperFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealArrayTypeDetector((List<Object>) currentValue, context, elementMapperFactory);
	}

	@Override
	public Object createDefaultValue() {
		return new ArrayList<>();
	}

}
