package com.github.rcd47.x2data.lib.unreal.mapper;

class IXComIndexObjectReferenceMapperFactory implements IUnrealFieldMapperFactory {

	private Class<?> referencedObjectType;
	
	IXComIndexObjectReferenceMapperFactory(Class<?> referencedObjectType) {
		this.referencedObjectType = referencedObjectType;
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new IXComIntObjectReferenceMapper(context);
	}
	
	class IXComIntObjectReferenceMapper extends UnrealPrimitiveMapperBase {
		IXComIntObjectReferenceMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitHistoryObjectValue(int objectIndex) {
			visitValue(context.referenceResolver.createIndexReference(referencedObjectType, objectIndex));
		}
	}

}
