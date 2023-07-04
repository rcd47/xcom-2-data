package com.github.rcd47.x2data.lib.unreal.mapper;

import java.nio.ByteBuffer;

import com.github.rcd47.x2data.lib.unreal.IUnrealObjectVisitor;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

interface IUnrealFieldMapper extends IUnrealObjectVisitor {
	
	default void up(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitStructStart(UnrealName type) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitStructEnd() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitDynamicArrayStart(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitDynamicArrayEnd() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitMapStart(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitMapEnd() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitUnparseableData(ByteBuffer data) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitBooleanValue(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitByteValue(byte value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitEnumValue(UnrealName enumType, UnrealName value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitFloatValue(float value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitDoubleValue(double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitIntValue(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitNameValue(UnrealName value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitStringValue(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitBasicInterfaceValue(UnrealName objectName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitBasicObjectValue(UnrealName objectName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitHistoryInterfaceValue(int objectIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void visitHistoryObjectValue(int objectIndex) {
		throw new UnsupportedOperationException();
	}
	
}
