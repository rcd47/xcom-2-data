package com.github.rcd47.x2data.lib.unreal;

import java.nio.ByteBuffer;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public interface IUnrealObjectVisitor {
	
	default void visitStructStart(UnrealName type) {}
	
	default void visitStructEnd() {}
	
	default void visitDynamicArrayStart(int size) {}
	
	default void visitDynamicArrayEnd() {}
	
	default void visitMapStart(int size) {}
	
	default void visitMapEnd() {}
	
	/**
	 * Called in two cases:
	 * <ol>
	 * <li>A dynamic array property is being visited, and there are no typings available.</li>
	 * <li>A struct that has untyped data at the end is being visited, and there are no typings available.</li>
	 * </ol>
	 * The buffer provided to this method may be reused, so if the data needs to be retained after this method
	 * returns, the method should make a copy and retain that copy instead.
	 */
	default void visitUnparseableData(ByteBuffer data) {}
	
	/**
	 * Begin visiting a property. The property's value is visited immediately after this.
	 */
	default void visitProperty(UnrealName propertyName, int staticArrayIndex) {}
	
	default void visitBooleanValue(boolean value) {}
	
	default void visitByteValue(byte value) {}
	
	default void visitEnumValue(UnrealName enumType, UnrealName value) {}
	
	default void visitFloatValue(float value) {}
	
	default void visitDoubleValue(double value) {}
	
	default void visitIntValue(int value) {}
	
	default void visitNameValue(UnrealName value) {}
	
	default void visitStringValue(String value) {}
	
	default void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {}
	
	default void visitBasicInterfaceValue(UnrealName objectName) {}
	
	default void visitBasicObjectValue(UnrealName objectName) {}
	
	default void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {}
	
	default void visitHistoryInterfaceValue(int objectIndex) {}
	
	default void visitHistoryObjectValue(int objectIndex) {}
	
}
