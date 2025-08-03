package com.github.rcd47.x2data.explorer.file;

import java.util.List;

import com.github.rcd47.x2data.explorer.file.data.PrimitiveInterner;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

public class HistoryFile {
	
	private final int randomSeed;
	private final int numArchivedFrames;
	private final List<HistoryFrame> frames;
	private final List<HistorySingletonObject> singletons;
	private final List<HistoryFileProblem> problems;
	private final List<UnrealName> contextTypes;
	private final List<UnrealName> objectTypes;
	private final PrimitiveInterner interner;
	
	public HistoryFile(int randomSeed, int numArchivedFrames, List<HistoryFrame> frames,
			List<HistorySingletonObject> singletons, List<HistoryFileProblem> problems, List<UnrealName> contextTypes,
			List<UnrealName> objectTypes, PrimitiveInterner interner) {
		this.randomSeed = randomSeed;
		this.numArchivedFrames = numArchivedFrames;
		this.frames = frames;
		this.singletons = singletons;
		this.problems = problems;
		this.contextTypes = contextTypes;
		this.objectTypes = objectTypes;
		this.interner = interner;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public int getNumArchivedFrames() {
		return numArchivedFrames;
	}

	public List<HistoryFrame> getFrames() {
		return frames;
	}

	public List<HistorySingletonObject> getSingletons() {
		return singletons;
	}

	public List<HistoryFileProblem> getProblems() {
		return problems;
	}

	public List<UnrealName> getContextTypes() {
		return contextTypes;
	}

	public List<UnrealName> getObjectTypes() {
		return objectTypes;
	}

	public PrimitiveInterner getInterner() {
		return interner;
	}
	
}
