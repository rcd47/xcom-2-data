package com.github.rcd47.x2data.lib.unreal.typings;

public class UnrealUntypedPropertyInfo {
	
	/*
	 * Types below can be:
	 * - UnrealDataType
	 * - String (struct type name)
	 * - Double.class (since Unrealscript does not have this type)
	 * - Map.class (for propertyType only, since Unrealscript does not have this type)
	 */
	
	public final int position;
	public final String propertyName;
	public final Object propertyType;
	public final Object arrayElementType;
	public final Object mapKeyType;
	public final Object mapValueType;
	
	UnrealUntypedPropertyInfo(int position, String propertyName, Object propertyType, Object arrayElementType,
			Object mapKeyType, Object mapValueType) {
		this.position = position;
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		this.arrayElementType = arrayElementType;
		this.mapKeyType = mapKeyType;
		this.mapValueType = mapValueType;
	}
	
}
