package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealBooleanMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealBooleanMapperFactory INSTANCE = new UnrealBooleanMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealBooleanMapper(mapperStack);
	}

	static class UnrealBooleanMapper extends UnrealPrimitiveMapperBase {
		UnrealBooleanMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitBooleanValue(boolean value) {
			visitValue(value);
		}
	}
	
}
