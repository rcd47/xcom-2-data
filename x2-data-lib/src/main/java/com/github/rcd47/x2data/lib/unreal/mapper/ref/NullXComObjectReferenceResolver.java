package com.github.rcd47.x2data.lib.unreal.mapper.ref;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

/**
 * Implementation of {@link IXComObjectReferenceResolver} that doesn't resolve anything.
 */
public class NullXComObjectReferenceResolver implements IXComObjectReferenceResolver {

	public static final NullXComObjectReferenceResolver INSTANCE = new NullXComObjectReferenceResolver();
	
	private NullXComObjectReferenceResolver() {}
	
	@Override
	public <T> IXComIndexObjectReference<T> createIndexReference(Class<T> referencedObjectType, int objectIndex) {
		return new IXComIndexObjectReference<T>() {
			@Override
			public T get() {
				return null;
			}

			@Override
			public int index() {
				return objectIndex;
			}
		};
	}

	@Override
	public <T> IXComStateObjectReference<T> createStateReference(Class<T> referencedObjectType, int objectId) {
		return new IXComStateObjectReference<T>() {
			@Override
			public T get() {
				return null;
			}

			@Override
			public int id() {
				return objectId;
			}
		};
	}

	@Override
	public <T> IXComRawStateObjectReference<T> createRawStateReference(Class<T> referencedObjectType, int objectId) {
		return new IXComRawStateObjectReference<T>() {
			@Override
			public T get() {
				return null;
			}

			@Override
			public int id() {
				return objectId;
			}
		};
	}

	@Override
	public <T> IXComNameObjectReference<T> createNameReference(Class<T> referencedObjectType, UnrealName objectName) {
		return new IXComNameObjectReference<T>() {
			@Override
			public T get() {
				return null;
			}

			@Override
			public UnrealName name() {
				return objectName;
			}
		};
	}

}
