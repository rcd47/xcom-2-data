package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class UnrealMapMapper implements IUnrealFieldMapper {
	
	// NOTE: map elements are not delta'd
	
	private Deque<IUnrealFieldMapper> mapperStack;
	private IUnrealFieldMapperFactory keyMapperFactory;
	private IUnrealFieldMapperFactory valueMapperFactory;
	private Map<Object, Object> map;
	private Object currentKey;
	private int remaining;

	UnrealMapMapper(Deque<IUnrealFieldMapper> mapperStack, IUnrealFieldMapperFactory keyMapperFactory,
			IUnrealFieldMapperFactory valueMapperFactory) {
		this.mapperStack = mapperStack;
		this.keyMapperFactory = keyMapperFactory;
		this.valueMapperFactory = valueMapperFactory;
	}

	@Override
	public void visitMapStart(int size) {
		map = new HashMap<>();
		if (size != 0) {
			remaining = size;
			mapperStack.push(keyMapperFactory.create(mapperStack, keyMapperFactory.createDefaultValue()));
		}
	}

	@Override
	public void visitMapEnd() {
		mapperStack.pop();
		mapperStack.peek().up(map);
	}

	@Override
	public void up(Object value) {
		if (currentKey == null) {
			currentKey = value;
			mapperStack.push(valueMapperFactory.create(mapperStack, valueMapperFactory.createDefaultValue()));
		} else {
			map.put(currentKey, value);
			currentKey = null;
			if (--remaining > 0) {
				mapperStack.push(keyMapperFactory.create(mapperStack, keyMapperFactory.createDefaultValue()));
			}
		}
	}
	
}
