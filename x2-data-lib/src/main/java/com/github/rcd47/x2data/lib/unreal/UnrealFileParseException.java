package com.github.rcd47.x2data.lib.unreal;

public class UnrealFileParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final long position;

	public UnrealFileParseException(Throwable cause, long position) {
		this(null, cause, position);
	}
	
	public UnrealFileParseException(String extraMessage, long position) {
		this(extraMessage, null, position);
	}
	
	public UnrealFileParseException(String extraMessage, Throwable cause, long position) {
		super("Parsing failure at position " + position + (extraMessage == null ? "" : ": " + extraMessage), cause);
		this.position = position;
	}

	public long getPosition() {
		return position;
	}
	
}
