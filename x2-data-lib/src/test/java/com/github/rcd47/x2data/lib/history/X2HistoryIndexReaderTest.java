package com.github.rcd47.x2data.lib.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_Unit;

public class X2HistoryIndexReaderTest {
	
	@Test
	public void testReadIndex() throws Exception {
		var reader = new X2HistoryReader();
		try (FileChannel in = FileChannel.open(
				Path.of("src/test/resources/x2hist/CI gatecrasher end tactical.x2hist"), StandardOpenOption.READ)) {
			var tempFile = Files.createTempFile(null, null);
			try (FileChannel decompressed = FileChannel.open(tempFile, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
				reader.decompress(in, decompressed);
				
				var index = reader.buildIndex(decompressed);
				
				var entries = index.getEntries();
				
				assertThat(entries).hasSize(11_810);
				assertThat(entries.get(0).getType()).isEqualTo(new UnrealName("XComGameStateHistory"));
				assertThat(entries.get(0).getMappedType()).isEqualTo(XComGameStateHistory.class);
				assertThat(entries.get(0).getPreviousVersionIndex()).isEqualTo(-1);
				assertThat(entries.get(0).getLength()).isEqualTo(12_635);
				assertThat(entries.get(0).getPosition()).isEqualTo(503_824);
				assertThat(entries.get(11_798).getType()).isEqualTo(new UnrealName("XComGameState_Unit"));
				assertThat(entries.get(11_798).getMappedType()).isEqualTo(XComGameState_Unit.class);
				assertThat(entries.get(11_798).getPreviousVersionIndex()).isEqualTo(11_757);
				assertThat(entries.get(11_809).getType()).isEqualTo(new UnrealName("XComGameState_X2S_EndTactical"));
				assertThat(entries.get(11_809).getPreviousVersionIndex()).isEqualTo(-1);
				assertThat(entries.get(11_809).getLength()).isEqualTo(445);
				assertThat(entries.get(11_809).getPosition()).isEqualTo(10_920_154);
			} finally {
				Files.deleteIfExists(tempFile);
			}
		}
	}
	
}
