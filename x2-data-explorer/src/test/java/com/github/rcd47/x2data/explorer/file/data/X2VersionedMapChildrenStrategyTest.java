package com.github.rcd47.x2data.explorer.file.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;

public class X2VersionedMapChildrenStrategyTest {
	
	@Test
	public void testNameStringEquivalence() {
		var map = new Object2ReferenceOpenCustomHashMap<>(new X2VersionedMapChildrenStrategy());
		
		map.put(new UnrealName("foo"), 1);
		map.put("bar", 2);
		
		assertThat(map).containsEntry("FOO", 1);
		assertThat(map).containsEntry(new UnrealName("BAR"), 2);
	}
	
}
