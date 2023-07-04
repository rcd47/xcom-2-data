package com.github.rcd47.x2data.lib.unreal.mapper;

class UnrealDoubleMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealDoubleMapperFactory INSTANCE = new UnrealDoubleMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealDoubleMapper(context);
	}

	static class UnrealDoubleMapper extends UnrealPrimitiveMapperBase {
		UnrealDoubleMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitDoubleValue(double value) {
			visitValue(value);
		}
	}
	
}
