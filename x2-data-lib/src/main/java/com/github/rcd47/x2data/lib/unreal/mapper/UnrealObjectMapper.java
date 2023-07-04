package com.github.rcd47.x2data.lib.unreal.mapper;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComIndexObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComNameObjectReference;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class UnrealObjectMapper {
	
	private UnrealObjectParser parser;
	private Map<Type, IUnrealFieldMapperFactory> mapperFactories;
	
	public UnrealObjectMapper(UnrealObjectParser parser) {
		this.parser = parser;
		
		mapperFactories = new HashMap<>();
		mapperFactories.put(UnrealName.class, UnrealNameMapperFactory.INSTANCE);
		mapperFactories.put(String.class, UnrealStringMapperFactory.INSTANCE);
		mapperFactories.put(Byte.class, UnrealByteMapperFactory.INSTANCE);
		mapperFactories.put(byte.class, UnrealByteMapperFactory.INSTANCE);
		mapperFactories.put(Integer.class, UnrealIntegerMapperFactory.INSTANCE);
		mapperFactories.put(int.class, UnrealIntegerMapperFactory.INSTANCE);
		mapperFactories.put(Float.class, UnrealFloatMapperFactory.INSTANCE);
		mapperFactories.put(float.class, UnrealFloatMapperFactory.INSTANCE);
		mapperFactories.put(Double.class, UnrealDoubleMapperFactory.INSTANCE);
		mapperFactories.put(double.class, UnrealDoubleMapperFactory.INSTANCE);
		mapperFactories.put(Boolean.class, UnrealBooleanMapperFactory.INSTANCE);
		mapperFactories.put(boolean.class, UnrealBooleanMapperFactory.INSTANCE);
	}
	
	public <T> T create(Class<T> type, Path file, IXComObjectReferenceResolver refResolver) throws IOException {
		return create(type, readFile(file), refResolver);
	}
	
	public <T> T create(Class<T> type, ByteBuffer buffer, IXComObjectReferenceResolver refResolver) {
		UnrealRootMapper<T> rootMapper = new UnrealRootMapper<>();
		UnrealStructMapperFactory mapperFactory = (UnrealStructMapperFactory) getOrCreateMapperFactory(type);
		@SuppressWarnings("unchecked")
		T value = (T) mapperFactory.createDefaultValue();
		rootMapper.mapperStack.push(mapperFactory.create(new UnrealObjectMapperContext(rootMapper.mapperStack, refResolver), value));
		parser.parse(mapperFactory.getParserTypeName(), buffer, rootMapper);
		return rootMapper.object;
	}
	
	public <T> T update(T object, Path file, IXComObjectReferenceResolver refResolver) throws IOException {
		return update(object, readFile(file), refResolver);
	}
	
	public <T> T update(T object, ByteBuffer buffer, IXComObjectReferenceResolver refResolver) {
		UnrealRootMapper<T> rootMapper = new UnrealRootMapper<>();
		UnrealStructMapperFactory mapperFactory = (UnrealStructMapperFactory) getOrCreateMapperFactory(object.getClass());
		rootMapper.mapperStack.push(mapperFactory.create(new UnrealObjectMapperContext(rootMapper.mapperStack, refResolver), object));
		parser.parse(mapperFactory.getParserTypeName(), buffer, rootMapper);
		return rootMapper.object;
	}
	
	private <E extends Enum<E>> IUnrealFieldMapperFactory getOrCreateMapperFactory(Type type) {
		var factory = mapperFactories.get(type);
		if (factory != null) {
			return factory;
		}
		
		if (type instanceof Class<?> classType) {
			if (classType.isEnum()) {
				@SuppressWarnings("unchecked")
				var enumFactory = new UnrealEnumMapperFactory<>((Class<E>) classType);
				mapperFactories.put(classType, enumFactory);
				return enumFactory;
			}
			
			// must be struct
			var structFactory = new UnrealStructMapperFactory();
			mapperFactories.put(classType, structFactory); // put before populating in case of circular references
			UnrealStructMapperFactory.forType(classType, structFactory, this::getOrCreateMapperFactory);
			return structFactory;
		}
		
		if (type instanceof ParameterizedType paramType) {
			var rawClass = (Class<?>) paramType.getRawType();
			var referencedObjectType = (Class<?>) paramType.getActualTypeArguments()[0];
			IUnrealFieldMapperFactory refFactory;
			if (IXComNameObjectReference.class.equals(rawClass)) {
				refFactory = new IXComNameObjectReferenceMapperFactory(referencedObjectType);
			} else if (IXComStateObjectReference.class.equals(rawClass)) {
				refFactory = new IXComStateObjectReferenceMapperFactory(referencedObjectType);
			} else if (IXComIndexObjectReference.class.equals(rawClass)) {
				refFactory = new IXComIndexObjectReferenceMapperFactory(referencedObjectType);
			} else {
				throw new IllegalArgumentException("Not supported: " + paramType);
			}
			mapperFactories.put(paramType, refFactory);
			return refFactory;
		}
		
		throw new IllegalArgumentException("Not supported: " + type);
	}
	
	private static ByteBuffer readFile(Path file) throws IOException {
		return ByteBuffer.wrap(Files.readAllBytes(file));
	}
	
}
