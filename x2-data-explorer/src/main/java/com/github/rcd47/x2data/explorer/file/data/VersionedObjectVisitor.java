package com.github.rcd47.x2data.explorer.file.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Deque;

import com.github.rcd47.x2data.lib.unreal.IUnrealObjectVisitor;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class VersionedObjectVisitor implements IUnrealObjectVisitor {
	
	private PrimitiveInterner interner;
	private Deque<X2VersionedDataContainer<?>> objectStack = new ArrayDeque<>(6);
	private Deque<VisitorState> stateStack = new ArrayDeque<>(6);
	private int frame;
	private Object nextMapKey;
	private int nextStaticArrayIndex;
	private int deltaDisabledDepth;
	
	private static enum VisitorState {
		ARRAY_ELEMENT, MAP_KEY, MAP_VALUE
	}
	
	public VersionedObjectVisitor(PrimitiveInterner interner) {
		this.interner = interner;
	}

	public void setRootObject(int frameNum, X2VersionedMap obj) {
		if (!objectStack.isEmpty()) {
			throw new IllegalStateException("objectStack is not empty");
		}
		objectStack.push(obj);
		frame = frameNum;
	}

	@Override
	public void visitStructStart(UnrealName type) {
		if (!stateStack.isEmpty()) {
			switch (stateStack.pop()) {
				case MAP_VALUE -> {
					stateStack.push(VisitorState.MAP_KEY);
					var parentUntyped = objectStack.peek();
					if (parentUntyped instanceof X2VersionedMap parent) {
						objectStack.push(parent.getOrCreateChild(frame, nextMapKey, nextStaticArrayIndex, () -> new X2VersionedMap(frame)));
					} else if (parentUntyped instanceof X2VersionedStaticArray parent) {
						objectStack.push(parent.getOrCreateElement(frame, nextStaticArrayIndex, () -> new X2VersionedMap(frame)));
					} else {
						throw new IllegalStateException("Map value parent is unsupported type " + parentUntyped.getClass());
					}
				}
				case ARRAY_ELEMENT -> {
					stateStack.push(VisitorState.ARRAY_ELEMENT);
					objectStack.push(((X2VersionedDynamicArray) objectStack.peek()).getOrCreateElement(frame, () -> new X2VersionedMap(frame)));
				}
				default -> throw new IllegalStateException("Invalid state for struct start " + stateStack.peek());
			}
		}
		stateStack.push(VisitorState.MAP_KEY);
	}

	@Override
	public void visitStructEnd() {
		popAndCheckState(VisitorState.MAP_KEY);
	}

	@Override
	public void visitDynamicArrayStart(int size) {
		switch (stateStack.pop()) {
			case MAP_VALUE -> {
				stateStack.push(VisitorState.MAP_KEY);
				objectStack.push(((X2VersionedMap) objectStack.peek())
						.getOrCreateChild(frame, nextMapKey, nextStaticArrayIndex, () -> new X2VersionedDynamicArray(frame, size)));
			}
			default -> throw new IllegalStateException("Invalid state for dynamic array start " + stateStack.peek());
		}
		deltaDisabledDepth++;
		stateStack.push(VisitorState.ARRAY_ELEMENT);
	}

	@Override
	public void visitDynamicArrayEnd() {
		decrementDeltaDisabledDepth(VisitorState.ARRAY_ELEMENT);
	}

	@Override
	public void visitMapStart(int size) {
		deltaDisabledDepth++;
		visitStructStart(null);
	}

	@Override
	public void visitMapEnd() {
		decrementDeltaDisabledDepth(VisitorState.MAP_KEY);
	}

	@Override
	public void visitUnparseableData(ByteBuffer data) {
		// decided not to intern these for now
		// if their mem usage is a problem, creating proper mappings would probably be a better solution
		data = ByteBuffer.allocate(data.remaining()).order(ByteOrder.LITTLE_ENDIAN).put(data).flip();
		if (stateStack.peek() == VisitorState.MAP_KEY) {
			((X2VersionedMap) objectStack.peek())
					.getOrCreateChild(frame, "native vars", 0, () -> new X2VersionedPrimitive())
					.setValueAt(frame, data);
		} else {
			visitPrimitiveValue(data);
		}
	}

	@Override
	public void visitProperty(UnrealName propertyName, int staticArrayIndex) {
		nextStaticArrayIndex = staticArrayIndex;
		visitPrimitiveValue(interner.internName(propertyName));
	}

	@Override
	public void visitBooleanValue(boolean value) {
		visitPrimitiveValue(value); // no need to intern boolean since Java caches the boxed objects
	}

	@Override
	public void visitByteValue(byte value) {
		visitPrimitiveValue(value); // no need to intern byte since Java caches the boxed objects
	}

	@Override
	public void visitEnumValue(UnrealName enumType, UnrealName value) {
		visitPrimitiveValue(interner.internName(value));
	}

	@Override
	public void visitFloatValue(float value) {
		visitPrimitiveValue(interner.internFloat(value));
	}

	@Override
	public void visitDoubleValue(double value) {
		visitPrimitiveValue(interner.internDouble(value));
	}

	@Override
	public void visitIntValue(int value) {
		visitPrimitiveValue(interner.internInt(value));
	}

	@Override
	public void visitNameValue(UnrealName value) {
		visitPrimitiveValue(interner.internName(value));
	}

	@Override
	public void visitStringValue(String value) {
		visitPrimitiveValue(interner.internString(value));
	}

	@Override
	public void visitBasicDelegateValue(UnrealName delegateName, String declaringClass) {
		visitPrimitiveValue(interner.internName(delegateName));
	}

	@Override
	public void visitBasicInterfaceValue(UnrealName objectName) {
		visitPrimitiveValue(interner.internName(objectName));
	}

	@Override
	public void visitBasicObjectValue(UnrealName objectName) {
		visitPrimitiveValue(interner.internName(objectName));
	}

	@Override
	public void visitHistoryDelegateValue(int objectIndex, UnrealName delegateName, String declaringClass) {
		visitPrimitiveValue(interner.internInt(objectIndex));
	}

	@Override
	public void visitHistoryInterfaceValue(int objectIndex) {
		visitPrimitiveValue(interner.internInt(objectIndex));
	}

	@Override
	public void visitHistoryObjectValue(int objectIndex) {
		visitPrimitiveValue(interner.internInt(objectIndex));
	}
	
	private void decrementDeltaDisabledDepth(VisitorState expectedState) {
		popAndCheckState(expectedState);
		deltaDisabledDepth--;
	}
	
	private void popAndCheckState(VisitorState expectedState) {
		var actualState = stateStack.pop();
		if (actualState != expectedState) {
			throw new IllegalStateException("Expected state " + expectedState + " but found " + actualState);
		}
		objectStack.pop().frameFinished(frame, deltaDisabledDepth > 0);
	}
	
	private void visitPrimitiveValue(Object value) {
		var state = stateStack.pop();
		switch (state) {
			case MAP_KEY -> {
				nextMapKey = value;
				stateStack.push(VisitorState.MAP_VALUE);
			}
			case MAP_VALUE -> {
				((X2VersionedMap) objectStack.peek())
						.getOrCreateChild(frame, nextMapKey, nextStaticArrayIndex, () -> new X2VersionedPrimitive())
						.setValueAt(frame, value);
				stateStack.push(VisitorState.MAP_KEY);
			}
			case ARRAY_ELEMENT -> {
				((X2VersionedDynamicArray) objectStack.peek())
						.getOrCreateElement(frame, () -> new X2VersionedPrimitive())
						.setValueAt(frame, value);
				stateStack.push(VisitorState.ARRAY_ELEMENT);
			}
		}
	}
	
}
