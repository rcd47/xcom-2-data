package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealStaticArrayMapper implements IUnrealFieldMapper {
	
	private UnrealName expectedPropertyName;
	private List<Object> currentValue;
	private UnrealObjectMapperContext context;
	private IUnrealFieldMapperFactory elementMapperFactory;
	private int currentIndex;

	UnrealStaticArrayMapper(UnrealName expectedPropertyName, List<Object> currentValue,
			UnrealObjectMapperContext context, IUnrealFieldMapperFactory elementMapperFactory) {
		// shallow clone the list
		// note that we init list fields to empty lists
		// but we can't know here that empty means it's new. could also mean it's a dynamic array that got emptied.
		currentValue = new ArrayList<>(currentValue);
		
		this.expectedPropertyName = expectedPropertyName;
		this.currentValue = currentValue;
		this.context = context;
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public void up(Object value) {
		currentValue.set(currentIndex, value);
	}

	@Override
	public void visitStructEnd() {
		// static array was at the end of a struct
		endOfArray();
		context.mapperStack.peek().visitStructEnd();
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		// static array was at the end of a struct that has unparseable data
		endOfArray();
		context.mapperStack.peek().visitUnparseableData(data);
	}

	@Override
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		if (!expectedPropertyName.equals(propertyName)) {
			// reached the end of the array
			endOfArray();
			context.mapperStack.peek().visitProperty(propertyName, staticArrayIndex);
			return;
		}
		
		currentIndex = staticArrayIndex;
		while (currentValue.size() <= currentIndex) {
			currentValue.add(elementMapperFactory.createDefaultValue());
		}
		context.mapperStack.push(elementMapperFactory.create(context, currentValue.get(currentIndex)));
	}
	
	private void endOfArray() {
		context.mapperStack.pop();
		context.mapperStack.peek().up(currentValue);
	}
	
}
