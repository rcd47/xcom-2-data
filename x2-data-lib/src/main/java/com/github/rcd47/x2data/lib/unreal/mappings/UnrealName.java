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
		return normalized.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof UnrealName other && normalized.equals(other.normalized));
	}

	@Override
	public String toString() {
		return "UnrealName [original=" + original + ", normalized=" + normalized + "]";
	}
	
	public static UnrealName from(Object object) {
		return object instanceof UnrealName n ? n : new UnrealName(object.toString());
	}
	
}
