package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.Deque;
import java.util.List;

class UnrealArrayTypeDetector implements IUnrealFieldMapper {
	
	private String propertyName;
	private int staticArrayIndex;
	private List<Object> currentValue;
	private Deque<IUnrealFieldMapper> mapperStack;
	private IUnrealFieldMapperFactory elementMapperFactory;

	UnrealArrayTypeDetector(List<Object> currentValue, Deque<IUnrealFieldMapper> mapperStack,
			IUnrealFieldMapperFactory elementMapperFactory) {
		this.currentValue = currentValue;
		this.mapperStack = mapperStack;
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {
		this.propertyName = propertyName;
		this.staticArrayIndex = staticArrayIndex;
	}

	private void replaceSelf(IUnrealFieldMapper replacement) {
		mapperStack.pop();
		mapperStack.push(replacement);
	}
	
	private void replaceSelfWithStaticArray() {
		replaceSelf(new UnrealStaticArrayMapper(propertyName, currentValue, mapperStack, elementMapperFactory));
		mapperStack.peek().visitProperty(propertyName, staticArrayIndex);
	}
	
	@Override
	public void visitDynamicArrayStart(int size) {
		replaceSelf(new UnrealDynamicArrayMapper(mapperStack, elementMapperFactory));
		mapperStack.peek().visitDynamicArrayStart(size);
	}

	@Override
	public void visitStructStart(String type) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitStructStart(type);
	}

	@Override
	public void visitBooleanValue(boolean value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitBooleanValue(value);
	}

	@Override
	public void visitByteValue(byte value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitByteValue(value);
	}

	@Override
	public void visitEnumValue(String enumType, String value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitEnumValue(enumType, value);
	}

	@Override
	public void visitFloatValue(float value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitFloatValue(value);
	}

	@Override
	public void visitDoubleValue(double value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitDoubleValue(value);
	}

	@Override
	public void visitIntValue(int value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitIntValue(value);
	}

	@Override
	public void visitNameValue(String value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitNameValue(value);
	}

	@Override
	public void visitStringValue(String value) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitStringValue(value);
	}

	@Override
	public void visitBasicDelegateValue(String delegateName, String declaringClass) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitBasicDelegateValue(delegateName, declaringClass);
	}

	@Override
	public void visitBasicInterfaceValue(String objectName) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitBasicInterfaceValue(objectName);
	}

	@Override
	public void visitBasicObjectValue(String objectName) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitBasicObjectValue(objectName);
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitHistoryDelegateValue(objectIndex, delegateName, declaringClass);
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitHistoryInterfaceValue(objectIndex);
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		replaceSelfWithStaticArray();
		mapperStack.peek().visitHistoryObjectValue(objectIndex);
	}
	
}
