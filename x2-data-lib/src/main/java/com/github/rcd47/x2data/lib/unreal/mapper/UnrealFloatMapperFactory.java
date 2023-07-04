package com.github.rcd47.x2data.lib.unreal.mapper;

class UnrealFloatMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealFloatMapperFactory INSTANCE = new UnrealFloatMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealFloatMapper(context);
	}

	static class UnrealFloatMapper extends UnrealPrimitiveMapperBase {
		UnrealFloatMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitFloatValue(float value) {
			visitValue(value);
		}
	}
	
}
