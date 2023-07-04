package com.github.rcd47.x2data.lib.unreal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypeInformer;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealUntypedPropertyInfo;

public class UnrealObjectParser {
	
	private boolean historyFormat;
	private Map<UnrealName, UnrealTypeInformer> typings;
	
	public UnrealObjectParser(boolean historyFormat, Map<UnrealName, UnrealTypeInformer> typings) {
		this.historyFormat = historyFormat;
		this.typings = typings;
	}

	public void parse(UnrealName objectType, ByteBuffer buffer, IUnrealObjectVisitor visitor) {
		// history objects start with an unknown int. seems to always be -1.
		// BasicSaveObject files have the version passed to BasicSaveObject followed by that same unknown int.
		buffer.order(ByteOrder.LITTLE_ENDIAN).position(buffer.position() + (historyFormat ? 4 : 8));
		try {
			parseStruct(objectType, buffer, visitor, true);
		} catch (Exception e) {
			throw new UnrealFileParseException(e, buffer.position());
		}
	}
	
	private void parseStruct(UnrealName objectType, ByteBuffer buffer, IUnrealObjectVisitor visitor, boolean remainingDataIsUnparseable) {
		visitor.visitStructStart(objectType);
		
		var typeInformer = typings.get(objectType);
		Iterator<UnrealUntypedPropertyInfo> untypedProperties = null;
		
		// some structs (e.g. Vector) are completely untyped and don't even have the None sentinel
		// if we have typings available, that will tell us whether this is the case
		// otherwise, we try to detect the answer by checking to see if the struct starts with a valid string
		if (typeInformer == null) {
			typeInformer = UnrealTypeInformer.UNKNOWN;
			int firstNameLength = buffer.getInt(buffer.position());
			if (firstNameLength >= buffer.remaining() || firstNameLength <= 0) {
				untypedProperties = typeInformer.untypedProperties.iterator();
			}
		} else if (typeInformer.isUntypedStruct) {
			untypedProperties = typeInformer.untypedProperties.iterator();
		}
		
		while (buffer.hasRemaining()) {
			if (untypedProperties == null) {
				UnrealName propertyName = readName(buffer);
				
				if (UnrealName.NONE.equals(propertyName)) {
					// None is a sentinel value that indicates the end of typed properties in a struct/object
					// there can be untyped properties after this (e.g. XComGameState_Unit's UnitValues)
					untypedProperties = typeInformer.untypedProperties.iterator();
					continue;
				}
				
				UnrealDataType propertyType = UnrealDataType.valueOf(readName(buffer).getNormalized());
				int propertyDataLength = buffer.getInt();
				int staticArrayIndex = buffer.getInt();
				
				visitor.visitProperty(propertyName, staticArrayIndex);
				
				parseValue(propertyType, propertyDataLength, typeInformer.arrayElementTypes.get(propertyName), buffer, visitor, false, false);
			} else if (untypedProperties.hasNext()) {
				var propertyInfo = untypedProperties.next();
				
				visitor.visitProperty(propertyInfo.propertyName, 0);
				
				if (propertyInfo.propertyType.equals(Map.class)) {
					int mapSize = buffer.getInt();
					visitor.visitMapStart(mapSize);
					for (int i = 0; i < mapSize; i++) {
						parseValue(propertyInfo.mapKeyType, 0, null, buffer, visitor, true, false);
						parseValue(propertyInfo.mapValueType, 0, null, buffer, visitor, true, false);
					}
					visitor.visitMapEnd();
				} else {
					parseValue(propertyInfo.propertyType, 0, propertyInfo.arrayElementType, buffer, visitor, true, false);
				}
			} else {
				// for dynamic arrays and untyped properties, we don't know the actual length of the struct
				// so if there really is unparseable data, we're screwed because we can't skip it
				if (remainingDataIsUnparseable) {
					visitor.visitUnparseableData(buffer.slice().order(ByteOrder.LITTLE_ENDIAN));
				}
				break;
			}
		}
		
		visitor.visitStructEnd();
	}
	
	private void parseValue(Object propertyType, int propertyDataLength, Object arrayElementType, ByteBuffer buffer,
			IUnrealObjectVisitor visitor, boolean inUntypedProperty, boolean forDynamicArrayElement) {
		if (propertyType instanceof UnrealDataType unrealType) {
			switch (unrealType) {
				case arrayproperty:
					if (arrayElementType == null) { // can't parse without knowing the element type
						if (propertyDataLength == 0) { // can't skip without knowing the array length
							throw new UnrealFileParseException("Cannot parse or skip dynamic array", buffer.position());
						}
						visitor.visitUnparseableData(buffer.slice(buffer.position(), propertyDataLength).order(ByteOrder.LITTLE_ENDIAN));
						buffer.position(buffer.position() + propertyDataLength);
						return;
					}
					int length = buffer.getInt();
					visitor.visitDynamicArrayStart(length);
					for (int i = 0; i < length; i++) {
						parseValue(arrayElementType, 0, null, buffer, visitor, inUntypedProperty, true);
					}
					visitor.visitDynamicArrayEnd();
					break;
				case boolproperty:
					visitor.visitBooleanValue(buffer.get() == 1);
					break;
				case byteproperty:
					/*
					 * Could be a byte or an enum.
					 * This logic is invoked for the following cases:
					 * 1) Property is a single byte.
					 * 2) Property is a single enum, and typings are not available. Implies the property is typed.
					 * 3) Property is a dynamic array of bytes.
					 * Other cases require typings and are handled differently, later in this method.
					 */
					UnrealName enumType;
					if (forDynamicArrayElement || (enumType = readName(buffer)).equals(UnrealName.NONE)) {
						// really is a byte
						visitor.visitByteValue(buffer.get());
					} else {
						// actually an enum string
						visitor.visitEnumValue(enumType, readName(buffer));
					}
					break;
				case delegateproperty:
					if (historyFormat) {
						visitor.visitHistoryDelegateValue(buffer.getInt(), readName(buffer), UnrealUtils.readString(buffer));
					} else {
						visitor.visitBasicDelegateValue(readName(buffer), UnrealUtils.readString(buffer));
					}
					break;
				case floatproperty:
					visitor.visitFloatValue(buffer.getFloat());
					break;
				case interfaceproperty:
					if (historyFormat) {
						visitor.visitHistoryInterfaceValue(buffer.getInt());
					} else {
						visitor.visitBasicInterfaceValue(readName(buffer));
					}
					break;
				case intproperty:
					visitor.visitIntValue(buffer.getInt());
					break;
				case nameproperty:
					visitor.visitNameValue(readName(buffer));
					break;
				case objectproperty:
					if (historyFormat) {
						visitor.visitHistoryObjectValue(buffer.getInt());
					} else {
						visitor.visitBasicObjectValue(readName(buffer));
					}
					break;
				case strproperty:
					visitor.visitStringValue(UnrealUtils.readString(buffer));
					break;
				case structproperty:
					var structType = readName(buffer);
					var structBuffer = buffer.slice(buffer.position(), propertyDataLength).order(ByteOrder.LITTLE_ENDIAN);
					try {
						parseStruct(structType, structBuffer, visitor, !inUntypedProperty && !forDynamicArrayElement);
					} catch (Exception e) {
						throw new UnrealFileParseException("Failure reading struct type " + structType, e, structBuffer.position());
					}
					buffer.position(buffer.position() + propertyDataLength);
					break;
				default:
					throw new IllegalStateException("Unsupported type " + unrealType);
			}
		} else if (propertyType instanceof UnrealName structOrEnumType) {
			var typeInfo = typings.get(structOrEnumType);
			if (typeInfo.mappedType.isEnum()) {
				/*
				 * This logic is invoked for the following cases:
				 * 1) Property is a single enum, and typings are available.
				 *    In this situation, the property might be typed or untyped.
				 * 2) Property is a dynamic array of enums. Implies typings are available.
				 *    In this situation, the enum value is written but the enum type name is not, at least for typed properties.
				 *    Not sure about untyped properties since I haven't seen any to examine their behavior.
				 */
				if (forDynamicArrayElement) {
					visitor.visitEnumValue(structOrEnumType, readName(buffer));
				} else if (inUntypedProperty) {
					visitor.visitByteValue(buffer.get());
				} else {
					visitor.visitEnumValue(readName(buffer), readName(buffer));
				}
			} else {
				parseStruct(structOrEnumType, buffer, visitor, !inUntypedProperty && !forDynamicArrayElement);
			}
		} else { // must be Double.class
			visitor.visitDoubleValue(buffer.getDouble());
		}
	}
	
	private UnrealName readName(ByteBuffer buffer) {
		return new UnrealName(UnrealUtils.readString(buffer, historyFormat));
	}
	
}
