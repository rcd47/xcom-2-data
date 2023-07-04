package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealSkipMapper implements IUnrealFieldMapper {
	
	private UnrealObjectMapperContext context;
	private int depth;

	UnrealSkipMapper(UnrealObjectMapperContext context) {
		this.context = context;
	}

	private void increaseDepth() {
		depth++;
	}
	
	private void decreaseDepth() {
		if (--depth == 0) {
			context.mapperStack.pop();
		}
	}
	
	private void visitValue() {
		if (depth == 0) {
			context.mapperStack.pop();
		}
	}
	
	@Override
	public void visitStructStart(UnrealName type) {
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
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {}

	@Override
	public void visitBooleanValue(boolean value) {
		visitValue();
	}

	@Override
	public void visitByteValue(byte value) {
		visitValue();
	}

	@Override
	public void visitEnumValue(UnrealName enumType, UnrealName value) {
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
	public void visitNameValue(UnrealName value) {
		visitValue();
	}

	@Override
	public void visitStringValue(String value) {
		visitValue();
	}

	@Override
	public void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {
		visitValue();
	}

	@Override
	public void visitBasicInterfaceValue(UnrealName objectName) {
		visitValue();
	}

	@Override
	public void visitBasicObjectValue(UnrealName objectName) {
		visitValue();
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {
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
