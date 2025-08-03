package com.github.rcd47.x2data.explorer.file.data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMaps;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ReferenceMap;
import it.unimi.dsi.fastutil.floats.Float2ReferenceMaps;
import it.unimi.dsi.fastutil.floats.Float2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMaps;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

public class PrimitiveInterner {
	
	private final Float2ReferenceMap<Float> floats;
	private final Double2ReferenceMap<Double> doubles;
	private final Int2ReferenceMap<Integer> ints;
	private final ConcurrentMap<UnrealName, UnrealName> names;
	private final ConcurrentMap<String, String> strings;
	private final Int2ReferenceMap<UnrealName> treeNodeArrayIndexes;
	private final ConcurrentMap<Object, UnrealName> treeNodeMapKeys;
	
	public PrimitiveInterner() {
		floats = Float2ReferenceMaps.synchronize(new Float2ReferenceOpenHashMap<>());
		doubles = Double2ReferenceMaps.synchronize(new Double2ReferenceOpenHashMap<>());
		ints = Int2ReferenceMaps.synchronize(new Int2ReferenceOpenHashMap<>());
		names = new ConcurrentHashMap<>();
		strings = new ConcurrentHashMap<>();
		treeNodeArrayIndexes = Int2ReferenceMaps.synchronize(new Int2ReferenceOpenHashMap<>());
		treeNodeMapKeys = new ConcurrentHashMap<>();
	}
	
	public Float internFloat(float value) {
		return floats.computeIfAbsent(value, k -> k); // Function.identity() would trigger auto-boxing
	}
	
	public Double internDouble(double value) {
		return doubles.computeIfAbsent(value, k -> k); // Function.identity() would trigger auto-boxing
	}
	
	public Integer internInt(int value) {
		return ints.computeIfAbsent(value, k -> k); // Function.identity() would trigger auto-boxing
	}
	
	public UnrealName internName(UnrealName value) {
		return names.computeIfAbsent(value, Function.identity());
	}
	
	public String internString(String value) {
		return strings.computeIfAbsent(value, Function.identity());
	}
	
	public UnrealName internTreeNodeArrayIndex(int value) {
		return treeNodeArrayIndexes.computeIfAbsent(value, k -> new UnrealName(Integer.toString(k)));
	}
	
	public UnrealName internTreeNodeMapKey(Object value) {
		// most keys are names
		return value instanceof UnrealName n ? n : treeNodeMapKeys.computeIfAbsent(value, k -> new UnrealName(k.toString()));
	}
	
}
