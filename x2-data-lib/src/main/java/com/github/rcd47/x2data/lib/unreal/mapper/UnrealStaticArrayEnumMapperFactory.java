package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.EnumMap;

class UnrealStaticArrayEnumMapperFactory<E extends Enum<E>> implements IUnrealFieldMapperFactory {

	private IUnrealFieldMapperFactory elementMapperFactory;
	private Class<E> enumType;
	private E[] enumConsts;
	
	UnrealStaticArrayEnumMapperFactory(IUnrealFieldMapperFactory elementMapperFactory, Class<E> enumType) {
		this.elementMapperFactory = elementMapperFactory;
		this.enumType = enumType;
		enumConsts = enumType.getEnumConstants();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealStaticArrayEnumMapper<>((EnumMap<E, Object>) currentValue, context, elementMapperFactory, enumConsts);
	}

	@Override
	public Object createDefaultValue() {
		var map = new EnumMap<>(enumType);
		for (var enumConst : enumConsts) {
			map.put(enumConst, elementMapperFactory.createDefaultValue());
		}
		return map;
	}

}
