package com.github.rcd47.x2data.lib.unreal.typings;

import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class UnrealTypeInformer {
	
	public static final UnrealTypeInformer UNKNOWN = new UnrealTypeInformer(null, null, false, Map.of(), List.of());
	
	public final UnrealName unrealTypeName;
	public final Class<?> mappedType;
	public final boolean isUntypedStruct;
	public final Map<UnrealName, Object> arrayElementTypes;
	public final List<UnrealUntypedPropertyInfo> untypedProperties;
	
	UnrealTypeInformer(UnrealName unrealTypeName, Class<?> mappedType, boolean isUntypedStruct,
			Map<UnrealName, Object> arrayElementTypes, List<UnrealUntypedPropertyInfo> untypedProperties) {
		this.unrealTypeName = unrealTypeName;
		this.mappedType = mappedType;
		this.isUntypedStruct = isUntypedStruct;
		this.arrayElementTypes = arrayElementTypes;
		this.untypedProperties = untypedProperties;
	}
	
}
