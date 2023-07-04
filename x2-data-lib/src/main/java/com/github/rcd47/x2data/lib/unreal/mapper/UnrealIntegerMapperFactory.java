package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealIntegerMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealIntegerMapperFactory INSTANCE = new UnrealIntegerMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealIntegerMapper(context);
	}

	static class UnrealIntegerMapper extends UnrealPrimitiveMapperBase {
		UnrealIntegerMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitIntValue(int value) {
			visitValue(value);
		}

		@Override
		public void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {
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
