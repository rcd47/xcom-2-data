package com.github.rcd47.x2data.lib.unreal;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UnrealUtils {
	
	public static final int UNREAL_MAGIC_NUMBER = 0x9E2A83C1;
	
	public static String readString(ByteBuffer buffer) {
		return readString(buffer, false);
	}
	
	public static String readString(ByteBuffer buffer, boolean hasExtraInt) {
		Charset charset = StandardCharsets.ISO_8859_1;
		int nullTerminatorLength = 1;
		int length = buffer.getInt();
		if (length == 0) {
			// have actually seen StrProperty with length 0
			return "";
		}
		if (length < 0) {
			// negative length means UTF-16LE, and it's the number of code units (not bytes or chars)
			charset = StandardCharsets.UTF_16LE;
			length = -length * 2;
			nullTerminatorLength = 2;
		}
		byte[] strBytes = new byte[length - nullTerminatorLength];
		buffer.get(strBytes);
		buffer.position(buffer.position() + nullTerminatorLength);
		if (hasExtraInt) {
			buffer.position(buffer.position() + 4);
		}
		return new String(strBytes, charset);
	}
	
}
