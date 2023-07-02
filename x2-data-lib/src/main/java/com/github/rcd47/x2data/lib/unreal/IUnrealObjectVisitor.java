package com.github.rcd47.x2data.lib.unreal;

import java.nio.ByteBuffer;

public interface IUnrealObjectVisitor {
	
	default boolean normalizePropertyNames() {
		return true;
	}
	
	default void visitStructStart(String type) {}
	
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
	default void visitProperty(String propertyName, int staticArrayIndex) {}
	
	default void visitBooleanValue(boolean value) {}
	
	default void visitByteValue(byte value) {}
	
	default void visitEnumValue(String enumType, String value) {}
	
	default void visitFloatValue(float value) {}
	
	default void visitDoubleValue(double value) {}
	
	default void visitIntValue(int value) {}
	
	/**
	 * Called when a name is encountered.
	 * Note that Unrealscript pools names and treats them as case-insensitive.
	 * If the same name is used in multiple places with different cases, the actual case used is the case
	 * from the first time Unrealscript encountered the name (i.e. when the name was added to the pool).
	 * Therefore, when comparing names, it is strongly recommended to either use {@link String#equalsIgnoreCase(String)}
	 * or convert all names to the same case using {@link String#toLowerCase(java.util.Locale)}.
	 */
	default void visitNameValue(String value) {}
	
	default void visitStringValue(String value) {}
	
	default void visitBasicDelegateValue(String delegateName, String declaringClass) {}
	
	default void visitBasicInterfaceValue(String objectName) {}
	
	default void visitBasicObjectValue(String objectName) {}
	
	default void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {}
	
	default void visitHistoryInterfaceValue(int objectIndex) {}
	
	default void visitHistoryObjectValue(int objectIndex) {}
	
}
