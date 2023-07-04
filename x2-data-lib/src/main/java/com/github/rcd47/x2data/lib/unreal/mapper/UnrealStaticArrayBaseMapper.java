package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

abstract class UnrealStaticArrayBaseMapper<X> implements IUnrealFieldMapper {
	
	private X currentValue;
	private UnrealObjectMapperContext context;
	private IUnrealFieldMapperFactory elementMapperFactory;
	private int currentIndex;

	protected UnrealStaticArrayBaseMapper(X currentValue, UnrealObjectMapperContext context,
			IUnrealFieldMapperFactory elementMapperFactory) {
		currentValue = cloneCurrentValue(currentValue);
		this.currentValue = currentValue;
		this.context = context;
		this.elementMapperFactory = elementMapperFactory;
	}

	protected abstract X cloneCurrentValue(X currentValue);
	
	protected abstract Object getItemAtIndex(X collection, int index);
	
	protected abstract void setItemAtIndex(X collection, int index, Object value);
	
	@Override
	public void up(Object value) {
		setItemAtIndex(currentValue, currentIndex, value);
		context.mapperStack.pop();
		context.mapperStack.peek().up(currentValue);
	}

	@Override
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		currentIndex = staticArrayIndex;
		context.mapperStack.push(elementMapperFactory.create(context, getItemAtIndex(currentValue, currentIndex)));
	}
	
}
