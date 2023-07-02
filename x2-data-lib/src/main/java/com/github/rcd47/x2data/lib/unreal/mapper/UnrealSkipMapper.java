package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;
import java.util.Deque;

class UnrealSkipMapper implements IUnrealFieldMapper {
	
	private Deque<IUnrealFieldMapper> mapperStack;
	private int depth;

	UnrealSkipMapper(Deque<IUnrealFieldMapper> mapperStack) {
		this.mapperStack = mapperStack;
	}

	private void increaseDepth() {
		depth++;
	}
	
	private void decreaseDepth() {
		if (--depth == 0) {
			mapperStack.pop();
		}
	}
	
	private void visitValue() {
		if (depth == 0) {
			mapperStack.pop();
		}
	}
	
	@Override
	public void visitStructStart(String type) {
		increaseDepth();
	}

	@Override
	public void visitStructEnd() {
		decreaseDepth();
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		increaseDepth();
	}

	@Override
	public void visitDynamicArrayEnd() {
		decreaseDepth();
	}

	@Override
	public void visitMapStart(int size) {
		increaseDepth();
	}

	@Override
	public void visitMapEnd() {
		decreaseDepth();
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		visitValue();
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {}

	@Override
	public void visitBooleanValue(boolean value) {
		visitValue();
	}

	@Override
	public void visitByteValue(byte value) {
		visitValue();
	}

	@Override
	public void visitEnumValue(String enumType, String value) {
		visitValue();
	}

	@Override
	public void visitFloatValue(float value) {
		visitValue();
	}

	@Override
	public void visitDoubleValue(double value) {
		visitValue();
	}

	@Override
	public void visitIntValue(int value) {
		visitValue();
	}

	@Override
	public void visitNameValue(String value) {
		visitValue();
	}

	@Override
	public void visitStringValue(String value) {
		visitValue();
	}

	@Override
	public void visitBasicDelegateValue(String delegateName, String declaringClass) {
		visitValue();
	}

	@Override
	public void visitBasicInterfaceValue(String objectName) {
		visitValue();
	}

	@Override
	public void visitBasicObjectValue(String objectName) {
		visitValue();
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {
		visitValue();
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		visitValue();
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		visitValue();
	}
	
}
