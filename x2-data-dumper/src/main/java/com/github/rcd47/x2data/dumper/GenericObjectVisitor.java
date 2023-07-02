package com.github.rcd47.x2data.dumper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.IUnrealObjectVisitor;

public class GenericObjectVisitor implements IUnrealObjectVisitor {
	
	GenericObject rootObject;
	private Deque<Object> objectStack = new ArrayDeque<>(6);
	private Deque<VisitorState> stateStack = new ArrayDeque<>(6);
	private Object nextPropertyOrKey;
	private int nextStaticArrayIndex;
	
	private static enum VisitorState {
		PROPERTY_NAME, PROPERTY_VALUE, ARRAY_ELEMENT, MAP_KEY, MAP_VALUE
	}
	
	GenericObjectVisitor(GenericObject rootObject) {
		this.rootObject = rootObject;
	}

	@Override
	public boolean normalizePropertyNames() {
		return false;
	}

	@Override
	public void visitStructStart(String type) {
		if (objectStack.isEmpty()) {
			rootObject = rootObject == null ? new GenericObject(type) : rootObject.deepClone();
			objectStack.push(rootObject);
			stateStack.push(VisitorState.PROPERTY_NAME);
		} else {
			GenericObject struct = null;
			if (stateStack.peek() == VisitorState.PROPERTY_VALUE) {
				// struct is a property or static array element
				// these are delta'd, so we must look for an existing value
				var existingValue = ((GenericObject) objectStack.peek()).properties.get(nextPropertyOrKey);
				if (existingValue != null) {
					if (existingValue instanceof GenericObject obj) { // property
						struct = obj;
					} else { // must be List (static array element)
						@SuppressWarnings("unchecked")
						List<GenericObject> list = (List<GenericObject>) existingValue;
						if (list.size() > nextStaticArrayIndex) {
							struct = list.get(nextStaticArrayIndex);
						}
					}
				}
			}
			if (struct == null) {
				struct = new GenericObject(type);
			}
			pushAndVisitValue(struct, VisitorState.PROPERTY_NAME);
		}
	}

	@Override
	public void visitStructEnd() {
		popAndCheckState(VisitorState.PROPERTY_NAME);
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		pushAndVisitValue(new ArrayList<>(size), VisitorState.ARRAY_ELEMENT);
	}

	@Override
	public void visitDynamicArrayEnd() {
		popAndCheckState(VisitorState.ARRAY_ELEMENT);
	}

	@Override
	public void visitMapStart(int size) {
		pushAndVisitValue(new HashMap<>(size), VisitorState.MAP_KEY);
	}

	@Override
	public void visitMapEnd() {
		popAndCheckState(VisitorState.MAP_KEY);
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		var copy = ByteBuffer.allocate(data.remaining()).order(ByteOrder.LITTLE_ENDIAN).put(data).flip();
		if (stateStack.peek() == VisitorState.PROPERTY_NAME) {
			((GenericObject) objectStack.peek()).unparseableData = copy;
		} else {
			visitValue(copy);
		}
	}

	@Override
	public void visitProperty(String propertyName, int staticArrayIndex) {
		var state = stateStack.pop();
		if (state != VisitorState.PROPERTY_NAME) {
			throw new IllegalStateException("Unexpected state: " + state);
		}
		nextPropertyOrKey = propertyName;
		nextStaticArrayIndex = staticArrayIndex;
		stateStack.push(VisitorState.PROPERTY_VALUE);
	}

	@Override
	public void visitBooleanValue(boolean value) {
		visitValue(value);
	}

	@Override
	public void visitByteValue(byte value) {
		visitValue(value);
	}

	@Override
	public void visitEnumValue(String enumType, String value) {
		visitValue(value);
	}

	@Override
	public void visitFloatValue(float value) {
		visitValue(value);
	}

	@Override
	public void visitDoubleValue(double value) {
		visitValue(value);
	}

	@Override
	public void visitIntValue(int value) {
		visitValue(value);
	}

	@Override
	public void visitNameValue(String value) {
		visitValue(value);
	}

	@Override
	public void visitStringValue(String value) {
		visitValue(value);
	}

	@Override
	public void visitBasicDelegateValue(String delegateName, String declaringClass) {
		visitValue(delegateName);
	}

	@Override
	public void visitBasicInterfaceValue(String objectName) {
		visitValue(objectName);
	}

	@Override
	public void visitBasicObjectValue(String objectName) {
		visitValue(objectName);
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, String delegateName, String declaringClass) {
		visitValue(objectIndex);
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		visitValue(objectIndex);
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		visitValue(objectIndex);
	}
	
	private void popAndCheckState(VisitorState expectedState) {
		var actualState = stateStack.pop();
		if (actualState != expectedState) {
			throw new IllegalStateException("Expected state " + expectedState + " but found " + actualState);
		}
		objectStack.pop();
	}
	
	private void pushAndVisitValue(Object value, VisitorState state) {
		visitValue(value);
		objectStack.push(value);
		stateStack.push(state);
	}
	
	@SuppressWarnings("unchecked")
	private void visitValue(Object value) {
		var state = stateStack.pop();
		if (state == VisitorState.PROPERTY_VALUE) {
			var properties = ((GenericObject) objectStack.peek()).properties;
			var existingValue = properties.get(nextPropertyOrKey);
			
			if (nextStaticArrayIndex > 0 && !(existingValue instanceof List)) {
				List<Object> list = new ArrayList<>();
				if (existingValue != null) {
					list.add(existingValue);
				}
				existingValue = list;
				properties.put((String) nextPropertyOrKey, list);
			}
			
			if (existingValue instanceof List && !(value instanceof List)) {
				List<Object> list = (List<Object>) existingValue;
				while (list.size() <= nextStaticArrayIndex) {
					list.add(null);
				}
				list.set(nextStaticArrayIndex, value);
			} else {
				properties.put((String) nextPropertyOrKey, value);
			}
			
			stateStack.push(VisitorState.PROPERTY_NAME);
		} else if (state == VisitorState.ARRAY_ELEMENT) {
			((List<Object>) objectStack.peek()).add(value);
			stateStack.push(VisitorState.ARRAY_ELEMENT);
		} else if (state == VisitorState.MAP_KEY) {
			nextPropertyOrKey = value;
			stateStack.push(VisitorState.MAP_VALUE);
		} else if (state == VisitorState.MAP_VALUE) {
			((Map<Object, Object>) objectStack.peek()).put(nextPropertyOrKey, value);
			stateStack.push(VisitorState.MAP_KEY);
		} else {
			throw new IllegalStateException("Unexpected state " + state);
		}
	}

}
