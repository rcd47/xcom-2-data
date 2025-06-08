package com.github.rcd47.x2data.lib.unreal.mapper;

class IXComRawStateObjectReferenceMapperFactory implements IUnrealFieldMapperFactory {

	private Class<?> referencedObjectType;
	
	IXComRawStateObjectReferenceMapperFactory(Class<?> referencedObjectType) {
		this.referencedObjectType = referencedObjectType;
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new IXComRawStateObjectReferenceMapper(context);
	}
	
	class IXComRawStateObjectReferenceMapper extends UnrealPrimitiveMapperBase {
		IXComRawStateObjectReferenceMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitIntValue(int value) {
			visitValue(context.referenceResolver.createStateReference(referencedObjectType, value));
		}
	}

}
