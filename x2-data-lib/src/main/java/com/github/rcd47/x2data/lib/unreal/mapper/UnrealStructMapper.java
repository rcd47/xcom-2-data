package com.github.rcd47.x2data.lib.unreal.mapper;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Map;

class UnrealStructMapper implements IUnrealFieldMapper {
	
	private Object struct;
	private Deque<IUnrealFieldMapper> mapperStack;
	private Map<String, UnrealStructField> fields;
	private Field currentField;

	UnrealStructMapper(Object struct, Deque<IUnrealFieldMapper> mapperStack,
			Map<String, UnrealStructField> fields) {
		try {
			// shallow clone the object
			// note that we init struct fields to an empty object, so struct will never be null here
			Object clone = struct.getClass().getConstructor().newInstance();
			for (var fieldInfo : fields.values()) {
				fieldInfo.field.set(clone, fieldInfo.field.get(struct));
			}
			struct = clone;
		} catch (Exception e) {
			// should never happen
			throw new RuntimeException(e);
		}
		
		this.struct = struct;
		this.mapperStack = mapperStack;
		this.fields = fields;
	}

	@Override
	public void up(Object value) {
		try {
			currentField.set(struct, value);
		} catch (Exception e) {
			// should not happen
			throw new RuntimeException(e);
		}
		currentField = null;
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {
		var fieldInfo = fields.get(propertyName);
		if (fieldInfo == null) {
			mapperStack.push(new UnrealSkipMapper(mapperStack));
			return;
		}
		
		try {
			currentField = fieldInfo.field;
			var fieldMapper = fieldInfo.mapperFactory.create(mapperStack, currentField.get(struct));
			mapperStack.push(fieldMapper);
			
			if (fieldMapper instanceof UnrealArrayTypeDetector) {
				fieldMapper.visitProperty(propertyName, staticArrayIndex);
			}
		} catch (Exception e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visitStructStart(String type) {}

	@Override
	public void visitStructEnd() {
		mapperStack.pop();
		mapperStack.peek().up(struct);
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {}
	
}
