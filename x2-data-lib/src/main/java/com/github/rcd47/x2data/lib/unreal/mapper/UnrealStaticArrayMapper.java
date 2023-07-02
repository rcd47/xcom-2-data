package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class UnrealStaticArrayMapper implements IUnrealFieldMapper {
	
	private String expectedPropertyName;
	private List<Object> currentValue;
	private Deque<IUnrealFieldMapper> mapperStack;
	private IUnrealFieldMapperFactory elementMapperFactory;
	private int currentIndex;

	UnrealStaticArrayMapper(String expectedPropertyName, List<Object> currentValue,
			Deque<IUnrealFieldMapper> mapperStack, IUnrealFieldMapperFactory elementMapperFactory) {
		// shallow clone the list
		// note that we init list fields to empty lists
		// but we can't know here that empty means it's new. could also mean it's a dynamic array that got emptied.
		currentValue = new ArrayList<>(currentValue);
		
		this.expectedPropertyName = expectedPropertyName;
		this.currentValue = currentValue;
		this.mapperStack = mapperStack;
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
		mapperStack.peek().visitStructEnd();
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		// static array was at the end of a struct that has unparseable data
		endOfArray();
		mapperStack.peek().visitUnparseableData(data);
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {
		if (!expectedPropertyName.equals(propertyName)) {
			// reached the end of the array
			endOfArray();
			mapperStack.peek().visitProperty(propertyName, staticArrayIndex);
			return;
		}
		
		currentIndex = staticArrayIndex;
		while (currentValue.size() <= currentIndex) {
			currentValue.add(elementMapperFactory.createDefaultValue());
		}
		mapperStack.push(elementMapperFactory.create(mapperStack, currentValue.get(currentIndex)));
	}
	
	private void endOfArray() {
		mapperStack.pop();
		mapperStack.peek().up(currentValue);
	}
	
}
