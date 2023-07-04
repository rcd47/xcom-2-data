package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.ArrayList;
import java.util.List;

class UnrealDynamicArrayMapper implements IUnrealFieldMapper {
	
	private List<Object> currentValue;
	private UnrealObjectMapperContext context;
	private IUnrealFieldMapperFactory elementMapperFactory;
	private int remaining;

	UnrealDynamicArrayMapper(UnrealObjectMapperContext context, IUnrealFieldMapperFactory elementMapperFactory) {
		this.context = context;
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public void up(Object value) {
		currentValue.add(value);
		if (--remaining > 0) {
			context.mapperStack.push(elementMapperFactory.create(context, elementMapperFactory.createDefaultValue()));
		}
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		currentValue = new ArrayList<>(size);
		if (size != 0) {
			// for dynamic arrays, individual entries are not delta'd
			context.mapperStack.push(elementMapperFactory.create(context, elementMapperFactory.createDefaultValue()));
			remaining = size;
		}
	}

	@Override
	public void visitDynamicArrayEnd() {
		context.mapperStack.pop();
		context.mapperStack.peek().up(currentValue);
	}
	
}
