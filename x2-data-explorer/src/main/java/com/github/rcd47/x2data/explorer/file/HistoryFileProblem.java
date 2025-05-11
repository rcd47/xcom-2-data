package com.github.rcd47.x2data.explorer.file;

public class HistoryFileProblem {
	
	private final HistoryFrame frame;
	private final String explanation;
	
	public HistoryFileProblem(HistoryFrame frame, String explanation) {
		this.frame = frame;
		this.explanation = explanation;
	}

	public HistoryFrame getFrame() {
		return frame;
	}

	public String getExplanation() {
		return explanation;
	}
	
}
