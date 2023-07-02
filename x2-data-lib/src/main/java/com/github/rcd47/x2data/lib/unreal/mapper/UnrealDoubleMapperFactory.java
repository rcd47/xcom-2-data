package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealDoubleMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealDoubleMapperFactory INSTANCE = new UnrealDoubleMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealDoubleMapper(mapperStack);
	}

	static class UnrealDoubleMapper extends UnrealPrimitiveMapperBase {
		UnrealDoubleMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitDoubleValue(double value) {
			visitValue(value);
		}
	}
	
}
