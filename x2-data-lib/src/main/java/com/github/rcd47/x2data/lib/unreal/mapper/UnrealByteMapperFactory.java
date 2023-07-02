package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealByteMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealByteMapperFactory INSTANCE = new UnrealByteMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealByteMapper(mapperStack);
	}

	static class UnrealByteMapper extends UnrealPrimitiveMapperBase {
		UnrealByteMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitByteValue(byte value) {
			visitValue(value);
		}
	}
	
}
