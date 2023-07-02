package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

class UnrealRootMapper<T> implements IUnrealFieldMapper {
	
	Deque<IUnrealFieldMapper> mapperStack;
	T object;
	
	public UnrealRootMapper() {
		mapperStack = new ArrayDeque<>(6);
		mapperStack.push(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void up(Object value) {
		object = (T) value;
	}

	@Override
	public void visitStructStart(String type) {
		mapperStack.peek().visitStructStart(type);
	}

	@Override
	public void visitStructEnd() {
		mapperStack.peek().visitStructEnd();
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		mapperStack.peek().visitDynamicArrayStart(size);
	}

	@Override
	public void visitDynamicArrayEnd() {
		mapperStack.peek().visitDynamicArrayEnd();
	}

	@Override
	public void visitMapStart(int size) {
		mapperStack.peek().visitMapStart(size);
	}

	@Override
	public void visitMapEnd() {
		mapperStack.peek().visitMapEnd();
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		mapperStack.peek().visitUnparseableData(data);
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {
		mapperStack.peek().visitProperty(propertyName, staticArrayIndex);
	}

	@Override
	public void visitBooleanValue(boolean value) {
		mapperStack.peek().visitBooleanValue(value);
	}

	@Override
	public void visitByteValue(byte value) {
		mapperStack.peek().visitByteValue(value);
	}

	@Override
	public void visitEnumValue(String enumType, String value) {
		mapperStack.peek().visitEnumValue(enumType, value);
	}

	@Override
	public void visitFloatValue(float value) {
		mapperStack.peek().visitFloatValue(value);
	}

	@Override
	public void visitDoubleValue(double value) {
		mapperStack.peek().visitDoubleValue(value);
	}

	@Override
	public void visitIntValue(int value) {
		mapperStack.peek().visitIntValue(value);
	}

	@Override
	public void visitNameValue(String value) {
		mapperStack.peek().visitNameValue(value);
	}

	@Override
	public void visitStringValue(String value) {
		mapperStack.peek().visitStringValue(value);
	}

	@Override
	public void visitBasicDelegateValue(String delegateName, String declaringClass) {
		mapperStack.peek().visitBasicDelegateValue(delegateName, declaringClass);
	}

	@Override
	public void visitBasicInterfaceValue(String objectName) {
		mapperStack.peek().visitBasicInterfaceValue(objectName);
	}

	@Override
	public void visitBasicObjectValue(String objectName) {
		mapperStack.peek().visitBasicObjectValue(objectName);
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {
		mapperStack.peek().visitHistoryDelegateValue(objectIndex, delegateName, declaringClass);
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		mapperStack.peek().visitHistoryInterfaceValue(objectIndex);
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		mapperStack.peek().visitHistoryObjectValue(objectIndex);
	}
	
}
