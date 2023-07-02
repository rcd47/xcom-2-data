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
		int length = buffer.getInt();
		if (length == 0) {
			// have actually seen StrProperty with length 0
			return "";
		}
		if (length < 0) {
			// not totally sure about this logic
			// several sources say negative length means Unicode, but haven't seen any actual examples to confirm
			// also not sure if UTF-16 is the correct encoding but several sources mention UTF-16 in relation to Unreal
			charset = StandardCharsets.UTF_16;
			length = -length;
		}
		byte[] strBytes = new byte[length - 1];
		buffer.get(strBytes);
		buffer.position(buffer.position() + (hasExtraInt ? 5 : 1)); // ignore null terminator and maybe the extra int
		return new String(strBytes, charset);
	}
	
}
