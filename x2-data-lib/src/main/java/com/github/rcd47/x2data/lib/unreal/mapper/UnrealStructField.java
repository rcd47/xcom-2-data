package com.github.rcd47.x2data.lib.unreal.mapper;

import java.lang.reflect.Field;

class UnrealStructField {
	
	final Field field;
	final IUnrealFieldMapperFactory mapperFactory;
	
	UnrealStructField(Field field, IUnrealFieldMapperFactory mapperFactory) {
		this.field = field;
		this.mapperFactory = mapperFactory;
	}
	
}
