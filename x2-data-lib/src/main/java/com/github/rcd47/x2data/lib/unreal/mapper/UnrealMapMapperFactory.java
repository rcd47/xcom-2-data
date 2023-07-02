package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;
import java.util.HashMap;

class UnrealMapMapperFactory implements IUnrealFieldMapperFactory {

	private IUnrealFieldMapperFactory keyMapperFactory;
	private IUnrealFieldMapperFactory valueMapperFactory;
	
	UnrealMapMapperFactory(IUnrealFieldMapperFactory keyMapperFactory,
			IUnrealFieldMapperFactory valueMapperFactory) {
		this.keyMapperFactory = keyMapperFactory;
		this.valueMapperFactory = valueMapperFactory;
	}

	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealMapMapper(mapperStack, keyMapperFactory, valueMapperFactory);
	}

	@Override
	public Object createDefaultValue() {
		return new HashMap<>();
	}

}
