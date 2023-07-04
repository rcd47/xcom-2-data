package com.github.rcd47.x2data.lib.unreal.mapper;

class UnrealBooleanMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealBooleanMapperFactory INSTANCE = new UnrealBooleanMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealBooleanMapper(context);
	}

	static class UnrealBooleanMapper extends UnrealPrimitiveMapperBase {
		UnrealBooleanMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitBooleanValue(boolean value) {
			visitValue(value);
		}
	}
	
}
