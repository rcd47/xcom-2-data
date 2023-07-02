package com.github.rcd47.x2data.lib.unreal.typings;

import java.util.List;
import java.util.Map;

public class UnrealTypeInformer {
	
	public static final UnrealTypeInformer UNKNOWN = new UnrealTypeInformer(null, null, false, false, Map.of(), List.of());
	
	public final String unrealTypeName;
	public final Class<?> mappedType;
	public final boolean isSingletonStateType;
	public final boolean isUntypedStruct;
	public final Map<String, Object> arrayElementTypes;
	public final List<UnrealUntypedPropertyInfo> untypedProperties;
	
	UnrealTypeInformer(String unrealTypeName, Class<?> mappedType, boolean isSingletonStateType,
			boolean isUntypedStruct, Map<String, Object> arrayElementTypes,
			List<UnrealUntypedPropertyInfo> untypedProperties) {
		this.unrealTypeName = unrealTypeName;
		this.mappedType = mappedType;
		this.isSingletonStateType = isSingletonStateType;
		this.isUntypedStruct = isUntypedStruct;
		this.arrayElementTypes = arrayElementTypes;
		this.untypedProperties = untypedProperties;
	}
	
}
