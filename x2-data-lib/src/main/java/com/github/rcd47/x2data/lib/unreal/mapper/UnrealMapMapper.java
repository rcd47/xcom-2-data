package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.HashMap;
import java.util.Map;

class UnrealMapMapper implements IUnrealFieldMapper {
	
	// NOTE: map elements are not delta'd
	
	private UnrealObjectMapperContext context;
	private IUnrealFieldMapperFactory keyMapperFactory;
	private IUnrealFieldMapperFactory valueMapperFactory;
	private Map<Object, Object> map;
	private Object currentKey;
	private int remaining;

	UnrealMapMapper(UnrealObjectMapperContext context, IUnrealFieldMapperFactory keyMapperFactory,
			IUnrealFieldMapperFactory valueMapperFactory) {
		this.context = context;
		this.keyMapperFactory = keyMapperFactory;
		this.valueMapperFactory = valueMapperFactory;
	}

	@Override
	public void visitMapStart(int size) {
		map = new HashMap<>();
		if (size != 0) {
			remaining = size;
			context.mapperStack.push(keyMapperFactory.create(context, keyMapperFactory.createDefaultValue()));
		}
	}

	@Override
	public void visitMapEnd() {
		context.mapperStack.pop();
		context.mapperStack.peek().up(map);
	}

	@Override
	public void up(Object value) {
		if (currentKey == null) {
			currentKey = value;
			context.mapperStack.push(valueMapperFactory.create(context, valueMapperFactory.createDefaultValue()));
		} else {
			map.put(currentKey, value);
			currentKey = null;
			if (--remaining > 0) {
				context.mapperStack.push(keyMapperFactory.create(context, keyMapperFactory.createDefaultValue()));
			}
		}
	}
	
}
