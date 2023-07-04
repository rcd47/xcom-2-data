package com.github.rcd47.x2data.lib.unreal.typings;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class UnrealTypingsBuilderTest {
	
	@Test
	public void testUnrealTypeNameAnnotation() {
		// Java class name is XComUnitValue
		assertThat(new UnrealTypingsBuilder().build(Set.of()).get(new UnrealName("UnitValue"))).isNotNull();
	}
	
	@Test
	public void testUnrealUntypedStructAnnotation() {
		assertThat(new UnrealTypingsBuilder().build(Set.of()).get(new UnrealName("Vector")).isUntypedStruct).isTrue();
	}
	
	@Test
	public void testUntypedElementsSimple() {
		var typing = new UnrealTypingsBuilder().build(Set.of()).get(new UnrealName("Vector"));
		
		assertThat(typing.untypedProperties).hasSize(3);
		
		assertThat(typing.untypedProperties.get(0).position).isEqualTo(1);
		assertThat(typing.untypedProperties.get(0).propertyName).isEqualTo(new UnrealName("X"));
		assertThat(typing.untypedProperties.get(0).propertyType).isEqualTo(UnrealDataType.floatproperty);
		assertThat(typing.untypedProperties.get(0).arrayElementType).isNull();
		assertThat(typing.untypedProperties.get(0).mapKeyType).isNull();
		assertThat(typing.untypedProperties.get(0).mapValueType).isNull();
		
		assertThat(typing.untypedProperties.get(1).position).isEqualTo(2);
		assertThat(typing.untypedProperties.get(1).propertyName).isEqualTo(new UnrealName("Y"));
		assertThat(typing.untypedProperties.get(1).propertyType).isEqualTo(UnrealDataType.floatproperty);
		assertThat(typing.untypedProperties.get(1).arrayElementType).isNull();
		assertThat(typing.untypedProperties.get(1).mapKeyType).isNull();
		assertThat(typing.untypedProperties.get(1).mapValueType).isNull();
		
		assertThat(typing.untypedProperties.get(2).position).isEqualTo(3);
		assertThat(typing.untypedProperties.get(2).propertyName).isEqualTo(new UnrealName("Z"));
		assertThat(typing.untypedProperties.get(2).propertyType).isEqualTo(UnrealDataType.floatproperty);
		assertThat(typing.untypedProperties.get(2).arrayElementType).isNull();
		assertThat(typing.untypedProperties.get(2).mapKeyType).isNull();
		assertThat(typing.untypedProperties.get(2).mapValueType).isNull();
	}
	
	@Test
	public void testUntypedElementsComplex() {
		var typing = new UnrealTypingsBuilder().build(Set.of()).get(new UnrealName("XComGameState_Unit"));
		
		assertThat(typing.untypedProperties).hasSize(1);
		
		assertThat(typing.untypedProperties.get(0).position).isEqualTo(1);
		assertThat(typing.untypedProperties.get(0).propertyName).isEqualTo(new UnrealName("UnitValues"));
		assertThat(typing.untypedProperties.get(0).propertyType).isEqualTo(Map.class);
		assertThat(typing.untypedProperties.get(0).arrayElementType).isNull();
		assertThat(typing.untypedProperties.get(0).mapKeyType).isEqualTo(UnrealDataType.nameproperty);
		assertThat(typing.untypedProperties.get(0).mapValueType).isEqualTo(new UnrealName("UnitValue"));
	}
	
	@Test
	public void testDynamicArrays() {
		var typing = new UnrealTypingsBuilder().build(Set.of()).get(new UnrealName("XComGameState_Unit"));
		assertThat(typing.arrayElementTypes).containsEntry(new UnrealName("m_SoldierProgressionAbilties"), new UnrealName("SCATProgression"));
	}
	
	@Test
	public void testDynamicArraysInheritance() {
		var typings = new UnrealTypingsBuilder().build(Set.of());
		assertThat(typings.get(new UnrealName("XComGameState_BaseObject")).arrayElementTypes)
				.containsEntry(new UnrealName("ComponentObjectIds"), UnrealDataType.intproperty);
		assertThat(typings.get(new UnrealName("XComGameState_Unit")).arrayElementTypes)
				.containsEntry(new UnrealName("ComponentObjectIds"), UnrealDataType.intproperty);
	}
	
}
