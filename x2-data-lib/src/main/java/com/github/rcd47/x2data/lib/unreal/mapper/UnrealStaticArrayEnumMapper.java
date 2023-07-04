package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.EnumMap;

class UnrealStaticArrayEnumMapper<E extends Enum<E>> extends UnrealStaticArrayBaseMapper<EnumMap<E, Object>> {

	private E[] enumConsts;
	
	UnrealStaticArrayEnumMapper(EnumMap<E, Object> currentValue, UnrealObjectMapperContext context,
			IUnrealFieldMapperFactory elementMapperFactory, E[] enumConsts) {
		super(currentValue, context, elementMapperFactory);
		this.enumConsts = enumConsts;
	}

	@Override
	protected EnumMap<E, Object> cloneCurrentValue(EnumMap<E, Object> currentValue) {
		return new EnumMap<>(currentValue);
	}

	@Override
	protected Object getItemAtIndex(EnumMap<E, Object> collection, int index) {
		return collection.get(enumConsts[index]);
	}

	@Override
	protected void setItemAtIndex(EnumMap<E, Object> collection, int index, Object value) {
		collection.put(enumConsts[index], value);
	}

}
