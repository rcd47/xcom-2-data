package com.github.rcd47.x2data.lib.unreal.mapper;

class UnrealByteMapperFactory implements IUnrealFieldMapperFactory {

	static final UnrealByteMapperFactory INSTANCE = new UnrealByteMapperFactory();
	
	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealByteMapper(context);
	}

	@Override
	public Object createDefaultValue() {
		return (byte) 0;
	}

	static class UnrealByteMapper extends UnrealPrimitiveMapperBase {
		UnrealByteMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitByteValue(byte value) {
			visitValue(value);
		}
	}
	
}
