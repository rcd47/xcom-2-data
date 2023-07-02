package com.github.rcd47.x2data.dumper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import picocli.CommandLine.IVersionProvider;

public class CliVersionProvider implements IVersionProvider {

	public static final String VERSION = loadVersion();
	
	@Override
	public String[] getVersion() throws Exception {
		return new String[] {VERSION};
	}
	
	private static String loadVersion() {
		try {
			return new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/version.txt"), StandardCharsets.UTF_8)).readLine();
		} catch (IOException e) {
			// should never happen
			throw new UncheckedIOException(e);
		}
	}

}
