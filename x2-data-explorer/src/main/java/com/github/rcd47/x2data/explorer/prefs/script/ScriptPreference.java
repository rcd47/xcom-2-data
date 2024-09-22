package com.github.rcd47.x2data.explorer.prefs.script;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class ScriptPreference {
	
	private static final Logger L = LogManager.getLogger(ScriptPreference.class);
	private static final GroovyShell GROOVY = new GroovyShell();
	
	private final Path prefFile;
	private final String defaultSource;
	private String source;
	private Script executable;
	
	ScriptPreference(Path prefFile, String defaultFile) {
		this.prefFile = prefFile;
		
		try (var in = getClass().getResourceAsStream(defaultFile)) {
			defaultSource = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// should never happen
			throw new UncheckedIOException(e);
		}
		
		source = defaultSource;
		if (Files.exists(prefFile)) {
			try {
				var prefSource = Files.readString(prefFile);
				executable = GROOVY.parse(prefSource);
				source = prefSource;
			} catch (IOException | CompilationFailedException e) {
				L.error("Failed to load script pref file {}. Will use default instead.", prefFile, e);
			}
		}
		if (executable == null) {
			executable = GROOVY.parse(source);
		}
	}
	
	public void setSource(String script) throws CompilationFailedException, IOException {
		// parse the script first, so we don't change source if parsing fails
		executable = GROOVY.parse(script);
		source = script;
		
		if (defaultSource.equals(script)) {
			Files.deleteIfExists(prefFile);
		} else {
			Files.writeString(prefFile, script);
		}
	}

	public String getDefaultSource() {
		return defaultSource;
	}

	public String getSource() {
		return source;
	}

	public Script getExecutable() {
		return executable;
	}

}
