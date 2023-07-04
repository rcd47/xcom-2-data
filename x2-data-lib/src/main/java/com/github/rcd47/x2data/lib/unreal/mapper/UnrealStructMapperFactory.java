package com.github.rcd47.x2data.lib.unreal.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;

class UnrealStructMapperFactory implements IUnrealFieldMapperFactory {
	
	private Class<?> type;
	private UnrealName parserTypeName;
	private Map<UnrealName, UnrealStructField> fields;
	
	static void forType(Class<?> structType, UnrealStructMapperFactory factory, Function<Type, IUnrealFieldMapperFactory> factoryFinder) {
		factory.type = structType;
		
		var nameAnnotation = structType.getAnnotation(UnrealTypeName.class);
		factory.parserTypeName = new UnrealName(nameAnnotation == null ? structType.getSimpleName() : nameAnnotation.value());
		
		factory.fields = new HashMap<>();
		for (var field : structType.getFields()) {
			IUnrealFieldMapperFactory fieldFactory;
			var fieldType = field.getGenericType();
			
			if (fieldType instanceof Class<?> fieldClass) {
				fieldFactory = factoryFinder.apply(fieldClass);
			} else if (fieldType instanceof ParameterizedType fieldParameters) {
				var rawType = fieldParameters.getRawType();
				var typeArgs = fieldParameters.getActualTypeArguments();
				if (List.class.equals(rawType)) {
					fieldFactory = new UnrealArrayTypeDetectorFactory(factoryFinder.apply(typeArgs[0]));
				} else if (Map.class.equals(rawType)) {
					fieldFactory = new UnrealMapMapperFactory(
							factoryFinder.apply(typeArgs[0]), factoryFinder.apply(typeArgs[1]));
				} else if (IXComObjectReference.class.isAssignableFrom((Class<?>) rawType)) {
					fieldFactory = factoryFinder.apply(fieldType);
				} else {
					throw new UnsupportedOperationException("Unexpected parameterized raw type " + fieldParameters + " for field " + field);
				}
			} else {
				throw new UnsupportedOperationException("Unexpected type " + fieldType + " for field " + field);
			}
			
			factory.fields.put(new UnrealName(field.getName()), new UnrealStructField(field, fieldFactory));
		}
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new UnrealStructMapper(currentValue, context, fields);
	}

	@Override
	public Object createDefaultValue() {
		try {
			Object struct = type.getConstructor().newInstance();
			for (var fieldInfo : fields.values()) {
				if (fieldInfo.field.get(struct) == null) {
					var defaultValue = fieldInfo.mapperFactory.createDefaultValue();
					if (defaultValue != null) {
						fieldInfo.field.set(struct, defaultValue);
					}
				}
			}
			return struct;
		} catch (Exception e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	public UnrealName getParserTypeName() {
		return parserTypeName;
	}
	
}
