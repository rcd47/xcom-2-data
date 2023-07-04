package com.github.rcd47.x2data.lib.unreal.mapper;

class UnrealStringMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealStringMapperFactory INSTANCE = new UnrealStringMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealStringMapper(context);
	}

	@Override
	public Object createDefaultValue() {
		return "";
	}

	static class UnrealStringMapper extends UnrealPrimitiveMapperBase {
		UnrealStringMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitStringValue(String value) {
			visitValue(value);
		}
	}
	
}
