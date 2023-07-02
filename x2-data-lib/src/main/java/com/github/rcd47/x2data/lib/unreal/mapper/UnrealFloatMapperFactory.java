package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealFloatMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealFloatMapperFactory INSTANCE = new UnrealFloatMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealFloatMapper(mapperStack);
	}

	static class UnrealFloatMapper extends UnrealPrimitiveMapperBase {
		UnrealFloatMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitFloatValue(float value) {
			visitValue(value);
		}
	}
	
}
