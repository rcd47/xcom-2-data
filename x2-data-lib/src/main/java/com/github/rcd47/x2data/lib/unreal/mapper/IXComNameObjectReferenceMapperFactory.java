package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class IXComNameObjectReferenceMapperFactory implements IUnrealFieldMapperFactory {

	private Class<?> referencedObjectType;
	
	IXComNameObjectReferenceMapperFactory(Class<?> referencedObjectType) {
		this.referencedObjectType = referencedObjectType;
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new IXComNameObjectReferenceMapper(context);
	}
	
	class IXComNameObjectReferenceMapper extends UnrealPrimitiveMapperBase {
		IXComNameObjectReferenceMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitBasicObjectValue(UnrealName objectName) {
			visitNameValue(objectName);
		}

		@Override
		public void visitNameValue(UnrealName value) {
			visitValue(context.referenceResolver.createNameReference(referencedObjectType, value));
		}
	}

}
