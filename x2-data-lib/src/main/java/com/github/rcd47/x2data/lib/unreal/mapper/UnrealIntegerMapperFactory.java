package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealIntegerMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealIntegerMapperFactory INSTANCE = new UnrealIntegerMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealIntegerMapper(mapperStack);
	}

	static class UnrealIntegerMapper extends UnrealPrimitiveMapperBase {
		UnrealIntegerMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitIntValue(int value) {
			visitValue(value);
		}

		@Override
		public void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {
			visitValue(objectIndex);
		}

		@Override
		public void visitHistoryInterfaceValue(int objectIndex) {
			visitValue(objectIndex);
		}

		@Override
		public void visitHistoryObjectValue(int objectIndex) {
			visitValue(objectIndex);
		}
	}
	
}
