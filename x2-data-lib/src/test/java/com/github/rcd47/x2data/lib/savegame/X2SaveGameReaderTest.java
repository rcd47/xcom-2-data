package com.github.rcd47.x2data.lib.savegame;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class X2SaveGameReaderTest {
	
	@Test
	public void testWotcTlpSave() throws Exception {
		try (FileChannel in = FileChannel.open(Path.of("src/test/resources/savegame/wotc-tlp.sav"), StandardOpenOption.READ)) {
			var header = new X2SaveGameReader().readHeader(in);
			
			assertThat(header.autoSave).isFalse();
			assertThat(header.campaignMonth).isEqualTo(1);
			assertThat(header.campaignNumber).isEqualTo(99);
			assertThat(header.campaignStartTime).isEqualTo(LocalDateTime.of(2022, 6, 4, 14, 27, 38));
			assertThat(header.creationTime).isEqualTo(LocalDateTime.of(2022, 6, 4, 14, 36));
			assertThat(header.description).isEqualTo("6/4/2022\n"
					+ "14:36\n"
					+ "x2s pre end tactical\n"
					+ "Break into ADVENT Compound and Rescue VIPs\n"
					+ "Operation Gatecrasher\n"
					+ "3/1/2035\n"
					+ "12:00 AM Wilderness of Western U.S. ");
			assertThat(header.gameVersion).isEqualTo(X2GameVersion.XCOM2_WOTC_TLP);
			assertThat(header.historyPosition).isEqualTo(8565);
			assertThat(header.installedDlcAndMods).hasSize(163);
			assertThat(header.ironmanEnabled).isFalse();
			assertThat(header.language).isEqualTo("INT");
			assertThat(header.mapCommand).isEqualTo("open Plot_WLD_Compound_ATT_Ravine?game=XComGame.XComTacticalGame?LoadingSave");
			assertThat(header.mapImage).isEqualTo("UILibrary_XPACK_MissionImages.Missions_WLD_MD_InterrogationCompound_01_Temp");
			assertThat(header.missionNumber).isEqualTo(1);
			assertThat(header.missionType).isEqualTo("GatecrasherCI");
			assertThat(header.name).isEqualTo("x2s pre end tactical");
			assertThat(header.saveSlot).isEqualTo(149);
			assertThat(header.tacticalAction).isEqualTo(2);
			assertThat(header.tacticalSave).isTrue();
			assertThat(header.tacticalTurn).isEqualTo(6);
			
			assertThat(in.position()).isEqualTo(8565);
		}
	}
	
	@Test
	public void testWotcSave() throws Exception {
		try (FileChannel in = FileChannel.open(Path.of("src/test/resources/savegame/wotc.sav"), StandardOpenOption.READ)) {
			var header = new X2SaveGameReader().readHeader(in);
			
			assertThat(header.autoSave).isFalse();
			assertThat(header.campaignMonth).isEqualTo(7);
			assertThat(header.campaignNumber).isEqualTo(1);
			assertThat(header.campaignStartTime).isEqualTo(LocalDateTime.of(2017, 8, 29, 17, 52, 23));
			assertThat(header.creationTime).isEqualTo(LocalDateTime.of(2017, 9, 3, 13, 3));
			assertThat(header.description).isEqualTo("9/3/2017\n"
					+ "13:03\n"
					+ "1-2\n"
					+ "Geoscape\n"
					+ "9/29/2035\n"
					+ "5:36 AM");
			assertThat(header.gameVersion).isEqualTo(X2GameVersion.XCOM2_WOTC);
			assertThat(header.historyPosition).isEqualTo(419);
			assertThat(header.installedDlcAndMods).extracting(p -> p.internalName)
					.containsExactly("DLC_3", "DLC_2", "DLC_1");
			assertThat(header.installedDlcAndMods).extracting(p -> p.friendlyName)
					.containsExactly("Shen's Last Gift", "Alien Hunters", "Anarchy's Children");
			assertThat(header.ironmanEnabled).isFalse();
			assertThat(header.language).isEqualTo("INT");
			assertThat(header.mapCommand).isEqualTo("open Avenger_Root?game=XComGame.XComHeadQuartersGame");
			assertThat(header.mapImage).isEqualTo("UILibrary_MissionImages.Missions_Strategy_Generic");
			assertThat(header.missionNumber).isEqualTo(40);
			assertThat(header.missionType).isEqualTo("Neutralize");
			assertThat(header.name).isEqualTo("1-2");
			assertThat(header.saveSlot).isEqualTo(1);
			assertThat(header.tacticalAction).isEqualTo(0);
			assertThat(header.tacticalSave).isFalse();
			assertThat(header.tacticalTurn).isEqualTo(0);
			
			assertThat(in.position()).isEqualTo(419);
		}
	}
	
	@Test
	public void testVanillaSave() throws Exception {
		try (FileChannel in = FileChannel.open(Path.of("src/test/resources/savegame/vanilla.ignore"), StandardOpenOption.READ)) {
			var header = new X2SaveGameReader().readHeader(in);
			
			assertThat(header.autoSave).isTrue();
			assertThat(header.campaignMonth).isEqualTo(0);
			assertThat(header.campaignNumber).isEqualTo(1);
			assertThat(header.campaignStartTime).isEqualTo(LocalDateTime.of(2016, 5, 14, 10, 0, 57));
			assertThat(header.creationTime).isEqualTo(LocalDateTime.of(2016, 5, 21, 18, 31));
			assertThat(header.description).isEqualTo("5/21/2016\n"
					+ "18:31\n"
					+ "AUTOSAVE: Campaign 1\n"
					+ "Geoscape\n"
					+ "11/1/2035\n"
					+ "1:50 PM");
			assertThat(header.gameVersion).isEqualTo(X2GameVersion.XCOM2);
			assertThat(header.historyPosition).isEqualTo(381);
			assertThat(header.installedDlcAndMods).extracting(p -> p.internalName).containsExactly("DLC_2", "DLC_1");
			assertThat(header.installedDlcAndMods).extracting(p -> p.friendlyName).containsExactly("Alien Hunters", "Anarchy's Children");
			assertThat(header.ironmanEnabled).isFalse();
			assertThat(header.language).isEqualTo("INT");
			assertThat(header.mapCommand).isEqualTo("open Avenger_Root?game=XComGame.XComHeadQuartersGame");
			assertThat(header.mapImage).isEqualTo("UILibrary_MissionImages.Missions_Strategy_Generic");
			assertThat(header.missionNumber).isEqualTo(0);
			assertThat(header.missionType).isNull();
			assertThat(header.name).isEqualTo("AUTOSAVE: Campaign 1");
			assertThat(header.saveSlot).isEqualTo(0);
			assertThat(header.tacticalAction).isEqualTo(0);
			assertThat(header.tacticalSave).isFalse();
			assertThat(header.tacticalTurn).isEqualTo(0);
			
			assertThat(in.position()).isEqualTo(381);
		}
	}
	
}
