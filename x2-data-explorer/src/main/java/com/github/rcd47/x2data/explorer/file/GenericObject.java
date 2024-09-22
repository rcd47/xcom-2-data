package com.github.rcd47.x2data.explorer.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class GenericObject {
	
	UnrealName type;
	Map<UnrealName, Object> properties = new HashMap<>();
	
	public GenericObject(UnrealName type) {
		this.type = type;
	}
	
	public GenericObject deepClone() {
		GenericObject clone = new GenericObject(type);
		properties.forEach((k, v) -> clone.properties.put(k, deepClone(v)));
		return clone;
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
	
}
