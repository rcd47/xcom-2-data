package com.github.rcd47.x2data.lib.unreal.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.UnrealObjectParserTest;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.ECharStatType;
import com.github.rcd47.x2data.lib.unreal.mappings.base.ECombatIntelligence;
import com.github.rcd47.x2data.lib.unreal.mappings.base.EMentalState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.EUnitValueCleanup;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_Unit;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;

public class UnrealObjectMapperTest {
	
	@Test
	public void testUnitTemplar() throws Exception {
		UnrealObjectMapper mapper = new UnrealObjectMapper(new UnrealObjectParser(false, new UnrealTypingsBuilder().build(Set.of())));
		XComGameState_Unit unit = mapper.create(
				XComGameState_Unit.class,
				UnrealObjectParserTest.loadFile("/basicSaveObject/XCGS_Unit_Templar.bin"),
				NullXComObjectReferenceResolver.INSTANCE);
		
		assertThat(unit).isNotNull();
		
		assertThat(unit.m_SoldierClassTemplateName).isNotNull();
		assertThat(unit.m_SoldierClassTemplateName.name()).isEqualTo(new UnrealName("Templar"));
		
		assertThat(unit.m_SoldierProgressionAbilties).hasSize(4);
		assertThat(unit.m_SoldierProgressionAbilties.get(0).iBranch).isEqualTo(0);
		assertThat(unit.m_SoldierProgressionAbilties.get(0).iRank).isEqualTo(0);
		assertThat(unit.m_SoldierProgressionAbilties.get(1).iBranch).isEqualTo(1);
		assertThat(unit.m_SoldierProgressionAbilties.get(1).iRank).isEqualTo(0);
		assertThat(unit.m_SoldierProgressionAbilties.get(2).iBranch).isEqualTo(2);
		assertThat(unit.m_SoldierProgressionAbilties.get(2).iRank).isEqualTo(0);
		assertThat(unit.m_SoldierProgressionAbilties.get(3).iBranch).isEqualTo(4);
		assertThat(unit.m_SoldierProgressionAbilties.get(3).iRank).isEqualTo(0);
		
		assertThat(unit.m_SoldierRank).isEqualTo(1);
		
		assertThat(unit.CharacterStats).hasSize(29);
		assertThat(unit.CharacterStats.get(ECharStatType.eStat_HP).BaseMaxValue).isEqualTo(6);
		assertThat(unit.CharacterStats.get(ECharStatType.eStat_HP).CurrentValue).isEqualTo(6);
		assertThat(unit.CharacterStats.get(ECharStatType.eStat_HP).MaxValue).isEqualTo(6);
		
		assertThat(unit.AcquiredTraits).isEmpty();
		
		assertThat(unit.AllSoldierBonds).hasSize(11);
		assertThat(unit.AllSoldierBonds.get(0).Bondmate).isNotNull();
		assertThat(unit.AllSoldierBonds.get(0).Bondmate.id()).isEqualTo(410);
		assertThat(unit.AllSoldierBonds.get(0).Compatibility).isEqualTo(1.9219538f);
		assertThat(unit.AllSoldierBonds.get(0).Cohesion).isZero();
		assertThat(unit.AllSoldierBonds.get(0).BondLevel).isZero();
		
		assertThat(unit.MentalState).isEqualTo(EMentalState.eMentalState_Ready);
		
		assertThat(unit.ComInt).isEqualTo(ECombatIntelligence.eComInt_Gifted);
		
		assertThat(unit.AbilityTree).hasSize(7);
		assertThat(unit.AbilityTree.get(0).Abilities).isNotEmpty();
		assertThat(unit.AbilityTree.get(0).Abilities.get(0).AbilityName).isEqualTo(new UnrealName("Rend"));
		assertThat(unit.AbilityTree.get(3).Abilities.get(1)).isNotNull();
		assertThat(unit.AbilityTree.get(3).Abilities.get(1).AbilityName).isEqualTo(UnrealName.EMPTY);
		
		assertThat(unit.strFirstName).isEqualTo("April");
		
		assertThat(unit.strLastName).isEqualTo("Riley");
		
		assertThat(unit.strNickName).isEqualTo("Stormwalker");
		
		assertThat(unit.strBackground).isEqualTo(
				"Country of Origin: \n"
				+ "Date of Birth: June 14, 2004\n"
				+ "\n"
				+ "Geist believes April is one of the more naturally gifted among her many followers. Long before encountering the Templars, April was already manipulating her surroundings with the power of Psionic energy.");
		
		assertThat(unit.nmCountry).isEqualTo(new UnrealName("Country_Templar"));
		
		assertThat(unit.UnitValues).containsOnlyKeys(new UnrealName("CH_StartMissionWill"));
		assertThat(unit.UnitValues.get(new UnrealName("CH_StartMissionWill")).eCleanup).isEqualTo(EUnitValueCleanup.eCleanup_Never);
		assertThat(unit.UnitValues.get(new UnrealName("CH_StartMissionWill")).fValue).isEqualTo(45);
	}
	
}
