package com.github.rcd47.x2data.dumper;

import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.tr;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import groovy.lang.GroovyObjectSupport;
import j2html.tags.specialized.TbodyTag;
import j2html.tags.specialized.TdTag;
import j2html.tags.specialized.ThTag;
import j2html.tags.specialized.TrTag;

public class GenericObject extends GroovyObjectSupport {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	
	public UnrealName type;
	public Map<UnrealName, Object> properties = new HashMap<>();
	public ByteBuffer unparseableData;
	
	private static enum ChangeType {
		CHANGED("table-info"),
		ADDED("table-success"),
		DELETED("table-danger");
		
		public final String rowStyle;

		private ChangeType(String rowStyle) {
			this.rowStyle = rowStyle;
		}
	}
	
	public GenericObject(UnrealName type) {
		this.type = type;
	}
	
	public GenericObject deepClone() {
		GenericObject clone = new GenericObject(type);
		clone.unparseableData = unparseableData;
		properties.forEach((k, v) -> clone.properties.put(k, deepClone(v)));
		return clone;
	}
	
	@Override
	public Object getProperty(String propertyName) {
		return properties.get(new UnrealName(propertyName));
	}

	private static Object deepClone(Object obj) {
		if (obj instanceof GenericObject o) {
			return o.deepClone();
		}
		if (obj instanceof List<?> l) {
			return l.stream().map(GenericObject::deepClone).collect(Collectors.toList());
		}
		// no need to clone Maps since those are only native and therefore are always replaced entirely
		return obj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenericObject other = (GenericObject) obj;
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
	
	public static void dump(TbodyTag tableBody, GenericObject obj) {
		dumpInternal("", tableBody, null, obj, false, false);
	}
	
	public static void dump(TbodyTag tableBody, GenericObject oldObj, GenericObject newObj, boolean onlyChangedProperties) {
		dumpInternal("", tableBody, oldObj, newObj, onlyChangedProperties, true);
	}
	
	private static void dumpInternal(
			String path, TbodyTag tableBody, GenericObject oldObj, GenericObject newObj, boolean onlyChangedProperties, boolean diff) {
		ChangeType changeType = oldObj == null ? ChangeType.ADDED : (newObj == null ? ChangeType.DELETED : ChangeType.CHANGED);
		Map<UnrealName, Object> oldProps = changeType == ChangeType.ADDED ? Map.of() : oldObj.properties;
		Map<UnrealName, Object> newProps = changeType == ChangeType.DELETED ? Map.of() : newObj.properties;
		
		TrTag row = tr();
		ThTag objectIdCell = null;
		int initialChildCount = 0;
		if (path.isEmpty()) {
			Integer objectId = (Integer) Objects.requireNonNullElse(oldObj, newObj).properties.get(OBJECT_ID);
			if (objectId != null) { // will be null for context objects
				initialChildCount = tableBody.getNumChildren();
				objectIdCell = th(objectId.toString());
				row.with(objectIdCell);
			}
		}
		
		row.with(td(path));
		if (diff) {
			row.withClass(changeType.rowStyle).with(changeType == ChangeType.ADDED ? td() : td(oldObj.type.getOriginal()));
		}
		row.with(changeType == ChangeType.DELETED ? td() : td(newObj.type.getOriginal()));
		tableBody.with(row);
		
		dumpProperties(path, tableBody, oldProps, newProps, onlyChangedProperties, diff);
		
		ByteBuffer oldUnparseableData = oldObj == null ? null : oldObj.unparseableData;
		ByteBuffer newUnparseableData = newObj == null ? null : newObj.unparseableData;
		if (oldUnparseableData != null || newUnparseableData != null) {
			dumpProperty(path + " native vars", tableBody, oldUnparseableData, newUnparseableData, onlyChangedProperties, diff);
		}
		
		if (objectIdCell != null) {
			objectIdCell.withRowspan(Integer.toString(tableBody.getNumChildren() - initialChildCount));
		}
	}
	
	private static void dumpProperties(String path, TbodyTag tableBody, Map<?, ?> oldProps, Map<?, ?> newProps,
			boolean onlyChangedProperties, boolean diff) {
		SortedSet<Object> keys = new TreeSet<>(oldProps.keySet());
		keys.addAll(newProps.keySet());
		
		for (Object key : keys) {
			Object oldValue = oldProps.get(key);
			Object newValue = newProps.get(key);
			
			if (onlyChangedProperties && Objects.equals(newValue, oldValue)) {
				continue;
			}
			
			String keyAsString = key instanceof UnrealName name ? name.getOriginal() : key.toString();
			String pathForKey = path.isEmpty() ? keyAsString : path + "." + keyAsString;
			
			if (newValue instanceof List || oldValue instanceof List) {
				List<?> newValueList = toList(newValue);
				List<?> oldValueList = toList(oldValue);
				int maxSize = Math.max(newValueList.size(), oldValueList.size());
				for (int i = 0; i < maxSize; i++) {
					Object oldItem = i < oldValueList.size() ? oldValueList.get(i) : null;
					Object newItem = i < newValueList.size() ? newValueList.get(i) : null;
					if (!Objects.equals(newItem, oldItem)) {
						dumpProperty(pathForKey + "[" + i + "]", tableBody, oldItem, newItem, onlyChangedProperties, diff);
					}
				}
			} else if (newValue instanceof Map || oldValue instanceof Map) {
				Map<?, ?> oldMap = Objects.requireNonNullElse((Map<?, ?>) oldValue, Map.of());
				Map<?, ?> newMap = Objects.requireNonNullElse((Map<?, ?>) newValue, Map.of());
				dumpProperties(pathForKey, tableBody, oldMap, newMap, onlyChangedProperties, diff);
			} else {
				dumpProperty(pathForKey, tableBody, oldValue, newValue, onlyChangedProperties, diff);
			}
		}
	}
	
	private static void dumpProperty(
			String path, TbodyTag tableBody, Object oldObj, Object newObj, boolean onlyChangedProperties, boolean diff) {
		try {
			if (oldObj instanceof GenericObject || newObj instanceof GenericObject) {
				dumpInternal(path, tableBody, (GenericObject) oldObj, (GenericObject) newObj, onlyChangedProperties, diff);
				return;
			}
			
			ChangeType changeType;
			if (Objects.equals(oldObj, newObj)) {
				if (onlyChangedProperties) {
					return;
				}
				changeType = null;
			} else if (oldObj == null) {
				changeType = ChangeType.ADDED;
			} else if (newObj == null) {
				changeType = ChangeType.DELETED;
			} else {
				changeType = ChangeType.CHANGED;
			}
			
			if (oldObj instanceof ByteBuffer || newObj instanceof ByteBuffer) {
				path += " (unparseable)";
				if (oldObj != null) {
					oldObj = bufferToString((ByteBuffer) oldObj);
				}
				if (newObj != null) {
					newObj = bufferToString((ByteBuffer) newObj);
				}
			}
			
			// else must be a primitive
			TrTag row = tr(td(path));
			if (diff) {
				if (changeType != null) {
					row.withClass(changeType.rowStyle);
				}
				TdTag oldCell = td().withClass("text-break");
				if (changeType != ChangeType.ADDED) {
					oldCell.withText(oldObj.toString());
				}
				row.with(oldCell);
			}
			TdTag newCell = td().withClass("text-break");
			if (changeType != ChangeType.DELETED) {
				newCell.withText(newObj.toString());
			}
			row.with(newCell);
			tableBody.with(row);
		} catch (Exception e) {
			throw new RuntimeException("Error dumping property at path " + path + ". Old value: " + oldObj + "; new value: " + newObj, e);
		}
	}
	
	private static String bufferToString(ByteBuffer buffer) {
		buffer.mark();
		String hex = Hex.encodeHexString(buffer, false);
		buffer.reset();
		return hex;
	}
	
	private static List<?> toList(Object obj) {
		if (obj instanceof List<?> l) {
			return l;
		}
		if (obj == null) {
			return List.of();
		}
		return List.of(obj);
	}
	
}
