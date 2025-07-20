package com.github.rcd47.x2data.explorer.file.data;

import java.util.Locale;
import java.util.Objects;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import it.unimi.dsi.fastutil.Hash.Strategy;

public class X2VersionedMapChildrenStrategy implements Strategy<Object> {

	@Override
	public int hashCode(Object o) {
		// make hash match for UnrealName and equivalent strings
		if (o instanceof String s) {
			// TODO maybe implement a cache for the lowercase conversion?
			return s.toLowerCase(Locale.ENGLISH).hashCode();
		}
		return o == null ? 0 : o.hashCode();
	}

	@Override
	public boolean equals(Object a, Object b) {
		if (a instanceof UnrealName name && b instanceof String string) {
			return string.equalsIgnoreCase(name.getOriginal());
		}
		if (b instanceof UnrealName name && a instanceof String string) {
			return string.equalsIgnoreCase(name.getOriginal());
		}
		return Objects.equals(a, b);
	}

}
