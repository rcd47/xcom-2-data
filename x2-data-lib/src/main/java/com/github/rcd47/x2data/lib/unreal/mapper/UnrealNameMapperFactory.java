package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealNameMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealNameMapperFactory INSTANCE = new UnrealNameMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealNameMapper(context);
	}

	@Override
	public Object createDefaultValue() {
		return UnrealName.EMPTY;
	}

	static class UnrealNameMapper extends UnrealPrimitiveMapperBase {
		UnrealNameMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitNameValue(UnrealName value) {
			visitValue(value);
		}

		@Override
		public void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {
			visitValue(delegateName);
		}

		@Override
		public void visitBasicInterfaceValue(UnrealName objectName) {
			visitValue(objectName);
		}

		@Override
		public void visitBasicObjectValue(UnrealName objectName) {
			visitValue(objectName);
		}
	}
	
}
