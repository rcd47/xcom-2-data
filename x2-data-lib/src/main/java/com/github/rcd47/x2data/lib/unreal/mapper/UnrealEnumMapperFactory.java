package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealEnumMapperFactory<E extends Enum<E>> implements IUnrealFieldMapperFactory {

	private Class<E> type;
	private E[] values;
	
	UnrealEnumMapperFactory(Class<E> type) {
		this.type = type;
		values = type.getEnumConstants();
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealEnumMapper<>(context, type, values);
	}

	@Override
	public Object createDefaultValue() {
		return values[0];
	}

	static class UnrealEnumMapper<E extends Enum<E>> extends UnrealPrimitiveMapperBase {
		private Class<E> type;
		private E[] values;

		public UnrealEnumMapper(UnrealObjectMapperContext context, Class<E> type, E[] values) {
			super(context);
			this.type = type;
			this.values = values;
		}

		@Override
		public void visitByteValue(byte value) {
			// untyped properties
			visitValue(values[value]);
		}

		@Override
		public void visitEnumValue(UnrealName enumType, UnrealName value) {
			// typed properties
			visitValue(Enum.valueOf(type, value.getOriginal()));
		}
	}
	
}
