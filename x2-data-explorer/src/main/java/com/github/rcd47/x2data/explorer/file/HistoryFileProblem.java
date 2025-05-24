package com.github.rcd47.x2data.explorer.file;

public class HistoryFileProblem {
	
	private final HistoryFrame frame;
	private final GameStateContext context;
	private final GameStateObject object;
	private final String explanation;
	
	public HistoryFileProblem(HistoryFrame frame, GameStateContext context, GameStateObject object,
			String explanation) {
		this.frame = frame;
		this.context = context;
		this.object = object;
		this.explanation = explanation;
	}

	public HistoryFrame getFrame() {
		return frame;
	}

	public GameStateContext getContext() {
		return context;
	}

	public GameStateObject getObject() {
		return object;
	}

	public String getExplanation() {
		return explanation;
	}
	
}
