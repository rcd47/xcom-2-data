package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;

class UnrealEnumMapperFactory<E extends Enum<E>> implements IUnrealFieldMapperFactory {

	private Class<E> type;
	private E[] values;
	
	UnrealEnumMapperFactory(Class<E> type) {
		this.type = type;
		values = type.getEnumConstants();
	}

	@Override
	public IUnrealFieldMapper create(Deque<IUnrealFieldMapper> mapperStack, Object currentValue) {
		return new UnrealEnumMapper<>(mapperStack, type, values);
	}

	@Override
	public Object createDefaultValue() {
		return values[0];
	}

	static class UnrealEnumMapper<E extends Enum<E>> extends UnrealPrimitiveMapperBase {
		private Class<E> type;
		private E[] values;

		public UnrealEnumMapper(Deque<IUnrealFieldMapper> mapperStack, Class<E> type, E[] values) {
			super(mapperStack);
			this.type = type;
			this.values = values;
		}

		@Override
		public void visitByteValue(byte value) {
			// untyped properties
			visitValue(values[value]);
		}

		@Override
		public void visitEnumValue(String enumType, String value) {
			// typed properties
			visitValue(Enum.valueOf(type, value));
		}
	}
	
}
