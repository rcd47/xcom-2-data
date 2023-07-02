package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class UnrealDynamicArrayMapper implements IUnrealFieldMapper {
	
	private List<Object> currentValue;
	private Deque<IUnrealFieldMapper> mapperStack;
	private IUnrealFieldMapperFactory elementMapperFactory;
	private int remaining;

	UnrealDynamicArrayMapper(Deque<IUnrealFieldMapper> mapperStack, IUnrealFieldMapperFactory elementMapperFactory) {
		this.mapperStack = mapperStack;
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public void up(Object value) {
		currentValue.add(value);
		if (--remaining > 0) {
			mapperStack.push(elementMapperFactory.create(mapperStack, elementMapperFactory.createDefaultValue()));
		}
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		currentValue = new ArrayList<>(size);
		if (size != 0) {
			// for dynamic arrays, individual entries are not delta'd
			mapperStack.push(elementMapperFactory.create(mapperStack, elementMapperFactory.createDefaultValue()));
			remaining = size;
		}
	}

	@Override
	public void visitDynamicArrayEnd() {
		mapperStack.pop();
		mapperStack.peek().up(currentValue);
	}
	
}
