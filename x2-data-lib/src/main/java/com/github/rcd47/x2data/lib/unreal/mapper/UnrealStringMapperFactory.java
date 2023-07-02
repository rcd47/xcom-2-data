package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealStringMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealStringMapperFactory INSTANCE = new UnrealStringMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealStringMapper(mapperStack);
	}

	@Override
	public Object createDefaultValue() {
		return "";
	}

	static class UnrealStringMapper extends UnrealPrimitiveMapperBase {
		UnrealStringMapper(Deque<IUnrealFieldMapper> mapperStack) {
			super(mapperStack);
		}

		@Override
		public void visitNameValue(String value) {
			visitValue(value);
		}

		@Override
		public void visitStringValue(String value) {
			visitValue(value);
		}

		@Override
		public void visitBasicDelegateValue(String delegateName, String declaringClass) {
			visitValue(delegateName);
		}

		@Override
		public void visitBasicInterfaceValue(String objectName) {
			visitValue(objectName);
		}

		@Override
		public void visitBasicObjectValue(String objectName) {
			visitValue(objectName);
		}
	}
	
}
