package com.github.rcd47.x2data.lib.unreal.mapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;

public class UnrealObjectMapper {
	
	private UnrealObjectParser parser;
	private Map<Class<?>, IUnrealFieldMapperFactory> mapperFactories;
	
	public UnrealObjectMapper(UnrealObjectParser parser) {
		this.parser = parser;
		
		mapperFactories = new HashMap<>();
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
	
	public <T> T create(Class<T> type, Path file) throws IOException {
		return create(type, readFile(file));
	}
	
	public <T> T create(Class<T> type, ByteBuffer buffer) {
		UnrealRootMapper<T> rootMapper = new UnrealRootMapper<>();
		UnrealStructMapperFactory mapperFactory = (UnrealStructMapperFactory) getOrCreateMapperFactory(type);
		@SuppressWarnings("unchecked")
		T value = (T) mapperFactory.createDefaultValue();
		rootMapper.mapperStack.push(mapperFactory.create(rootMapper.mapperStack, value));
		parser.parse(mapperFactory.getParserTypeName(), buffer, rootMapper);
		return rootMapper.object;
	}
	
	public <T> T update(T object, Path file) throws IOException {
		return update(object, readFile(file));
	}
	
	public <T> T update(T object, ByteBuffer buffer) {
		UnrealRootMapper<T> rootMapper = new UnrealRootMapper<>();
		UnrealStructMapperFactory mapperFactory = (UnrealStructMapperFactory) getOrCreateMapperFactory(object.getClass());
		rootMapper.mapperStack.push(mapperFactory.create(rootMapper.mapperStack, object));
		parser.parse(mapperFactory.getParserTypeName(), buffer, rootMapper);
		return rootMapper.object;
	}
	
	private <E extends Enum<E>> IUnrealFieldMapperFactory getOrCreateMapperFactory(Class<?> type) {
		var factory = mapperFactories.get(type);
		if (factory != null) {
			return factory;
		}
		
		if (type.isEnum()) {
			@SuppressWarnings("unchecked")
			var enumFactory = new UnrealEnumMapperFactory<>((Class<E>) type);
			mapperFactories.put(type, enumFactory);
			return enumFactory;
		}
		
		// must be struct
		var structFactory = new UnrealStructMapperFactory();
		mapperFactories.put(type, structFactory); // put before populating in case of circular references
		UnrealStructMapperFactory.forType(type, structFactory, this::getOrCreateMapperFactory);
		return structFactory;
	}
	
	private static ByteBuffer readFile(Path file) throws IOException {
		return ByteBuffer.wrap(Files.readAllBytes(file));
	}
	
}
