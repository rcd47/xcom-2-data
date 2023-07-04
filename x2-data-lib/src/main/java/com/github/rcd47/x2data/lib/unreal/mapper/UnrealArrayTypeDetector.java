package com.github.rcd47.x2data.lib.unreal.mapper;

import java.util.List;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealArrayTypeDetector implements IUnrealFieldMapper {
	
	private UnrealName propertyName;
	private int staticArrayIndex;
	private List<Object> currentValue;
	private UnrealObjectMapperContext context;
	private IUnrealFieldMapperFactory elementMapperFactory;

	UnrealArrayTypeDetector(List<Object> currentValue, UnrealObjectMapperContext context,
			IUnrealFieldMapperFactory elementMapperFactory) {
		this.currentValue = currentValue;
		this.context = context;
		this.elementMapperFactory = elementMapperFactory;
	}

	@Override
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		this.propertyName = propertyName;
		this.staticArrayIndex = staticArrayIndex;
	}

	private void replaceSelf(IUnrealFieldMapper replacement) {
		context.mapperStack.pop();
		context.mapperStack.push(replacement);
	}
	
	private void replaceSelfWithStaticArray() {
		replaceSelf(new UnrealStaticArrayMapper(propertyName, currentValue, context, elementMapperFactory));
		context.mapperStack.peek().visitProperty(propertyName, staticArrayIndex);
	}
	
	@Override
	public void visitDynamicArrayStart(int size) {
		replaceSelf(new UnrealDynamicArrayMapper(context, elementMapperFactory));
		context.mapperStack.peek().visitDynamicArrayStart(size);
	}

	@Override
	public void visitStructStart(UnrealName type) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitStructStart(type);
	}

	@Override
	public void visitBooleanValue(boolean value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitBooleanValue(value);
	}

	@Override
	public void visitByteValue(byte value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitByteValue(value);
	}

	@Override
	public void visitEnumValue(UnrealName enumType, UnrealName value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitEnumValue(enumType, value);
	}

	@Override
	public void visitFloatValue(float value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitFloatValue(value);
	}

	@Override
	public void visitDoubleValue(double value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitDoubleValue(value);
	}

	@Override
	public void visitIntValue(int value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitIntValue(value);
	}

	@Override
	public void visitNameValue(UnrealName value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitNameValue(value);
	}

	@Override
	public void visitStringValue(String value) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitStringValue(value);
	}

	@Override
	public void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitBasicDelegateValue(delegateName, declaringClass);
	}

	@Override
	public void visitBasicInterfaceValue(UnrealName objectName) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitBasicInterfaceValue(objectName);
	}

	@Override
	public void visitBasicObjectValue(UnrealName objectName) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitBasicObjectValue(objectName);
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitHistoryDelegateValue(objectIndex, delegateName, declaringClass);
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitHistoryInterfaceValue(objectIndex);
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		replaceSelfWithStaticArray();
		context.mapperStack.peek().visitHistoryObjectValue(objectIndex);
	}
	
}
