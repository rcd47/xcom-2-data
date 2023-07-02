package com.github.rcd47.x2data.lib.unreal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;

@ExtendWith(MockitoExtension.class)
public class UnrealObjectParserTest {
	
	@Mock
	private IUnrealObjectVisitor visitor;
	
	@Test
	public void testScalarProperties() throws Exception {
		byte[] abilityCostsBytes = new byte[] {
				0x02,0x00,0x00,0x00,0x1F,0x00,0x00,0x00,0x54,0x72,0x61,0x6E,0x73,0x69,0x65,0x6E,0x74,0x2E,0x58,0x32,0x41,0x62,0x69,
				0x6C,0x69,0x74,0x79,0x43,0x6F,0x73,0x74,0x5F,0x41,0x6D,0x6D,0x6F,0x5F,0x36,0x00,0x27,0x00,0x00,0x00,0x54,0x72,0x61,
				0x6E,0x73,0x69,0x65,0x6E,0x74,0x2E,0x58,0x32,0x41,0x62,0x69,0x6C,0x69,0x74,0x79,0x43,0x6F,0x73,0x74,0x5F,0x41,0x63,
				0x74,0x69,0x6F,0x6E,0x50,0x6F,0x69,0x6E,0x74,0x73,0x5F,0x39,0x00};
		
		UnrealObjectParser parser = new UnrealObjectParser(false, Map.of());
		parser.parse("X2AbilityTemplate", loadFile("/basicSaveObject/X2AbilityTemplate_Overwatch.bin"), visitor);
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitStructStart("X2AbilityTemplate");
		inOrder.verify(visitor).visitProperty("AbilityCosts", 0);
		inOrder.verify(visitor).visitUnparseableData(ByteBuffer.wrap(abilityCostsBytes));
		inOrder.verify(visitor).visitProperty("bDontDisplayInAbilitySummary", 0);
		inOrder.verify(visitor).visitBooleanValue(true);
		inOrder.verify(visitor).visitProperty("Hostility", 0);
		inOrder.verify(visitor).visitEnumValue("EAbilityHostility", "eHostility_Defensive");
		inOrder.verify(visitor).visitProperty("IconImage", 0);
		inOrder.verify(visitor).visitStringValue("img:///UILibrary_PerkIcons.UIPerk_overwatch");
		inOrder.verify(visitor).visitProperty("ShotHUDPriority", 0);
		inOrder.verify(visitor).visitIntValue(200);
		inOrder.verify(visitor).visitProperty("DataName", 0);
		inOrder.verify(visitor).visitNameValue("overwatch");
		inOrder.verify(visitor).visitProperty("ClassThatCreatedUs", 0);
		inOrder.verify(visitor).visitBasicObjectValue("XComGame.X2Ability_DefaultAbilitySet");
		inOrder.verify(visitor, never()).visitUnparseableData(any());
		inOrder.verify(visitor).visitStructEnd();
	}
	
	@Test
	public void testStaticArrayIndexAndNestedStruct() throws Exception {
		UnrealObjectParser parser = new UnrealObjectParser(false, Map.of());
		parser.parse("XComGameState_Unit", loadFile("/basicSaveObject/XCGS_Unit_Templar.bin"), visitor);
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitProperty("CharacterStats", 28);
		inOrder.verify(visitor).visitStructStart("CharacterStat");
		inOrder.verify(visitor).visitProperty("Type", 0);
		inOrder.verify(visitor).visitEnumValue("ECharStatType", "eStat_FlankingAimBonus");
		inOrder.verify(visitor).visitProperty("CachedMaxValueIsCurrent", 0);
		inOrder.verify(visitor).visitBooleanValue(true);
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitProperty("ComInt", 0);
	}
	
	@Test
	public void testDynamicArray() throws Exception {
		UnrealObjectParser parser = new UnrealObjectParser(false, new UnrealTypingsBuilder().build(Set.of()));
		parser.parse("XComGameState_Unit", loadFile("/basicSaveObject/XCGS_Unit_Templar.bin"), visitor);
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitProperty("m_SoldierProgressionAbilties", 0);
		inOrder.verify(visitor).visitDynamicArrayStart(4);
		inOrder.verify(visitor).visitStructStart("SCATProgression");
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitStructStart("SCATProgression");
		inOrder.verify(visitor).visitProperty("iBranch", 0);
		inOrder.verify(visitor).visitIntValue(1);
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitStructStart("SCATProgression");
		inOrder.verify(visitor).visitProperty("iBranch", 0);
		inOrder.verify(visitor).visitIntValue(2);
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitStructStart("SCATProgression");
		inOrder.verify(visitor).visitProperty("iBranch", 0);
		inOrder.verify(visitor).visitIntValue(4);
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitDynamicArrayEnd();
		inOrder.verify(visitor).visitProperty("m_SoldierRank", 0);
	}
	
	@Test
	public void testUntypedDataAtEndOfStruct() throws Exception {
		UnrealObjectParser parser = new UnrealObjectParser(false, new UnrealTypingsBuilder().build(Set.of()));
		parser.parse("XComGameState_Unit", loadFile("/basicSaveObject/XCGS_Unit_Templar.bin"), visitor);
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitProperty("UnitValues", 0);
		inOrder.verify(visitor).visitMapStart(1);
		inOrder.verify(visitor).visitNameValue("CH_StartMissionWill");
		inOrder.verify(visitor).visitStructStart("UnitValue");
		inOrder.verify(visitor).visitProperty("fValue", 0);
		inOrder.verify(visitor).visitFloatValue(45);
		inOrder.verify(visitor).visitProperty("eCleanup", 0);
		inOrder.verify(visitor).visitByteValue((byte) 2);
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verify(visitor).visitMapEnd();
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testUnparseableDataAtEndOfStruct() throws Exception {
		UnrealObjectParser parser = new UnrealObjectParser(false, Map.of());
		parser.parse("XComGameState_Unit", loadFile("/basicSaveObject/XCGS_Unit_Templar.bin"), visitor);
		
		var unparseableBytes = new byte[] {
				0x01,0x00,0x00,0x00,0x14,0x00,0x00,0x00,0x43,0x48,0x5F,0x53,0x74,0x61,0x72,0x74,0x4D,0x69,0x73,0x73,0x69,0x6F,0x6E,
				0x57,0x69,0x6C,0x6C,0x00,0x00,0x00,0x34,0x42,0x02};
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitUnparseableData(ByteBuffer.wrap(unparseableBytes));
		inOrder.verify(visitor).visitStructEnd();
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testDynamicArrayOfEnum() throws Exception {
		UnrealObjectParser parser = new UnrealObjectParser(true, new UnrealTypingsBuilder().build(Set.of()));
		parser.parse("AbilityResultContext", loadFile("/x2hist/AbilityResultContext.bin"), visitor);
		
		InOrder inOrder = Mockito.inOrder(visitor);
		inOrder.verify(visitor).visitProperty("MultiTargetHitResults", 0);
		inOrder.verify(visitor).visitDynamicArrayStart(1);
		inOrder.verify(visitor).visitEnumValue("EAbilityHitResult", "eHit_Success");
		inOrder.verify(visitor).visitDynamicArrayEnd();
		inOrder.verify(visitor).visitProperty("MultiTargetEffectResults", 0);
	}
	
	public static ByteBuffer loadFile(String path) throws IOException {
		return ByteBuffer.wrap(UnrealObjectParserTest.class.getResourceAsStream(path).readAllBytes());
	}
	
}
