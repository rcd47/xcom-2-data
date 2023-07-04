package com.github.rcd47.x2data.lib.unreal.mapper;

import com.github.rcd47.x2data.lib.unreal.mapper.ref.IXComStateObjectReference;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class IXComStateObjectReferenceMapperFactory implements IUnrealFieldMapperFactory {

	private static final UnrealName STATE_OBJ_REF_NAME = new UnrealName("StateObjectReference");
	private static final UnrealName OBJECT_ID_NAME = new UnrealName("ObjectID");
	
	private Class<?> referencedObjectType;
	
	IXComStateObjectReferenceMapperFactory(Class<?> referencedObjectType) {
		this.referencedObjectType = referencedObjectType;
	}

	@Override
	public IUnrealFieldMapper create(UnrealObjectMapperContext context, Object currentValue) {
		return new IXComStateObjectReferenceMapper(context);
	}
	
	class IXComStateObjectReferenceMapper extends UnrealPrimitiveMapperBase {
		private IXComStateObjectReference<?> ref;
		
		IXComStateObjectReferenceMapper(UnrealObjectMapperContext context) {
			super(context);
		}

		@Override
		public void visitStructStart(UnrealName type) {
			if (!STATE_OBJ_REF_NAME.equals(type)) {
				throw new IllegalArgumentException("Unexpected struct type " + type);
			}
		}

		@Override
		public void visitStructEnd() {
			visitValue(ref);
		}

		@Override
		public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
			if (!OBJECT_ID_NAME.equals(propertyName)) {
				throw new IllegalArgumentException("Unexpected property name " + propertyName);
			}
		}

		@Override
		public void visitIntValue(int value) {
			ref = context.referenceResolver.createStateReference(referencedObjectType, value);
		}
	}

}
