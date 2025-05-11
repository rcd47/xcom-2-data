package com.github.rcd47.x2data.explorer.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.rcd47.x2data.lib.savegame.X2SaveGameReader;

public class FileParsingRegressionTest {
	
	@ParameterizedTest
	@MethodSource("listSaveFiles")
	public void testSaveFileParsedSuccessfully(Path file) throws Exception {
		try (var in = FileChannel.open(file)) {
			// the main point is to ensure that no exceptions are thrown
			assertThat(new X2SaveGameReader().readHeader(in)).isNotNull();
			assertThat(new HistoryFileReader().read(in, _ -> {}, _ -> {})).isNotNull();
		}
	}
	
	public static Stream<Path> listSaveFiles() throws IOException {
		return Files
				.walk(Path.of("../x2-data-lib/src/test/resources/savegame"))
				.filter(p -> p.getFileName().toString().endsWith(".sav") && Files.isRegularFile(p));
	}
	
	@ParameterizedTest
	@MethodSource("listHistoryFiles")
	public void testHistoryFileParsedSuccessfully(Path file) throws Exception {
		try (var in = FileChannel.open(file)) {
			// the main point is to ensure that no exceptions are thrown
			assertThat(new HistoryFileReader().read(in, _ -> {}, _ -> {})).isNotNull();
		}
	}
	
	public static Stream<Path> listHistoryFiles() throws IOException {
		return Files
				.walk(Path.of("../x2-data-lib/src/test/resources/x2hist"))
				.filter(p -> p.getFileName().toString().endsWith(".x2hist") && Files.isRegularFile(p));
	}
	
}
