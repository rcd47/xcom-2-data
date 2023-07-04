package com.github.rcd47.x2data.lib.unreal.mappings;

import java.util.Locale;

public class UnrealName implements Comparable<UnrealName> {
	
	public static final UnrealName EMPTY = new UnrealName("");
	public static final UnrealName NONE = new UnrealName("None");
	
	private final String original;
	private final String normalized;
	
	public UnrealName(String value) {
		original = value;
		normalized = value.toLowerCase(Locale.ENGLISH);
	}

	public String getOriginal() {
		return original;
	}

	public String getNormalized() {
		return normalized;
	}

	@Override
	public int compareTo(UnrealName o) {
		return normalized.compareTo(o.normalized);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((normalized == null) ? 0 : normalized.hashCode());
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
		UnrealName other = (UnrealName) obj;
		if (normalized == null) {
			if (other.normalized != null) {
				return false;
			}
		} else if (!normalized.equals(other.normalized)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UnrealName [original=" + original + ", normalized=" + normalized + "]";
	}
	
}
