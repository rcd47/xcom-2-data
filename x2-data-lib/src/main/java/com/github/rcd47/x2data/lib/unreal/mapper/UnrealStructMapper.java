package com.github.rcd47.x2data.lib.unreal.mapper;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

class UnrealStructMapper implements IUnrealFieldMapper {
	
	private Object struct;
	private UnrealObjectMapperContext context;
	private Map<UnrealName, UnrealStructField> fields;
	private Field currentField;

	UnrealStructMapper(Object struct, UnrealObjectMapperContext context,
			Map<UnrealName, UnrealStructField> fields) {
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
		this.context = context;
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
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		var fieldInfo = fields.get(propertyName);
		if (fieldInfo == null) {
			context.mapperStack.push(new UnrealSkipMapper(context));
			return;
		}
		
		try {
			currentField = fieldInfo.field;
			var fieldMapper = fieldInfo.mapperFactory.create(context, currentField.get(struct));
			context.mapperStack.push(fieldMapper);
			
			if (fieldMapper instanceof UnrealStaticArrayBaseMapper) {
				fieldMapper.visitProperty(propertyName, staticArrayIndex);
			}
		} catch (Exception e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visitStructStart(UnrealName type) {}

	@Override
	public void visitStructEnd() {
		context.mapperStack.pop();
		context.mapperStack.peek().up(struct);
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {}
	
}
