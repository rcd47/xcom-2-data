package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;
import java.util.List;

class UnrealStaticArrayListMapper extends UnrealStaticArrayBaseMapper<List<Object>> {
	
	protected UnrealStaticArrayListMapper(List<Object> currentValue, UnrealObjectMapperContext context,
			IUnrealFieldMapperFactory elementMapperFactory) {
		super(currentValue, context, elementMapperFactory);
	}

	@Override
	protected List<Object> cloneCurrentValue(List<Object> currentValue) {
		return new ArrayList<>(currentValue);
	}

	@Override
	protected Object getItemAtIndex(List<Object> collection, int index) {
		return collection.get(index);
	}

	@Override
	protected void setItemAtIndex(List<Object> collection, int index, Object value) {
		collection.set(index, value);
	}
	
}
