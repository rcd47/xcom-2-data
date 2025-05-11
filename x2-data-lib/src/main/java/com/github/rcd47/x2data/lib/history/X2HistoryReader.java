package com.github.rcd47.x2data.lib.history;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.anarres.lzo.LzoDecompressor1x_safe;
import org.anarres.lzo.LzoTransformer;
import org.anarres.lzo.lzo_uintp;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.UnrealUtils;
import com.github.rcd47.x2data.lib.unreal.mapper.UnrealObjectMapper;
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_CampaignSettings;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypeInformer;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;

public class X2HistoryReader {
	
	private static final UnrealName CAMPAIGN_SETTINGS_NAME = new UnrealName("XComGameState_CampaignSettings");
	
	private ByteBuffer intBuffer;
	private ByteBuffer blockHeaderBuffer;
	private ByteBuffer compressedBlockBuffer;
	private ByteBuffer uncompressedBlockBuffer;
	private lzo_uintp lzoSize;
	
	public X2HistoryReader() {
		intBuffer = prepareBuffer(null, 4);
		blockHeaderBuffer = prepareBuffer(null, 24);
		compressedBlockBuffer = prepareBuffer(null, 131_072);
		uncompressedBlockBuffer = prepareBuffer(null, 131_072);
		lzoSize = new lzo_uintp();
	}

	/*
	 * Below determinations are the result of lots of staring at files in hex editors, plus some trial and error.
	 * It is incomplete and the complete parts may be incorrect.
	 * 
	 * A .x2hist file is created by XComGameStateHistory.WriteHistoryToFile().
	 * Save files use this format as well, but have an additional header section at the beginning of the file.
	 * 
	 * The data is a series of compressed blocks. Each block has the following format:
	 * 1. int: Unreal magic number
	 * 2. int: max uncompressed block size. Always seems to be 0x00002000 (131,072) bytes.
	 * 3. int: compressed size.
	 * 4. int: uncompressed size.
	 * 5. int: compressed size again.
	 * 6. int: uncompressed size again.
	 * Possibly the first pair is a total followed by a sequence of pairs, similar to compressed UPKs.
	 * But it seems like there's only ever 1 pair in the sequence.
	 * 7. N bytes of data compressed using LZO. The compressor is reset after each block.
	 * 
	 * Decompressed blocks are concatenated to yield readable data.
	 * The compression ratio is high: a 4.5 MB save file I used as a test became a 41 MB file after decompression.
	 * 
	 * The decompressed data contains the XComGameStateHistory followed by the X2EventManager.
	 * The X2EventManager is not very interesting. It looks like it just contains the list of registered event listeners.
	 * For XComGameStateHistory, the first object is always the XComGameStateHistory itself.
	 * 
	 * Both X2EventManager and XComGameStateHistory have the following format:
	 * 1. int: ArchiveFileVersion. Always 845.
	 * 2. int: ArchiveFileLicenseeVersion. Always 120 (for WOTC) or 108 (for pre-WOTC).
	 * 3. int: Number of objects that were saved.
	 * 4. list of object metadata: One entry for each object that was saved.
	 * 5. list of objects: The objects that were saved.
	 * 
	 * Each object metadata has the following format:
	 * 1. string: name of the object's class.
	 * 2. int: index of the object that referenced this object and caused it to be serialized. -1 for the root object.
	 * 3. int: unknown. Seems to always be -1.
	 * 4. int: unknown. Possibly a class ID. Value is the same for all objects of a given class but different for each class.
	 * 5. int: for delta objects, the index of the previous version of the object. -1 indicates that the object is not a delta.
	 * 
	 * Each object has the following format:
	 * 1. int: object size.
	 * 2. N bytes of data in the same format as produced by Engine.BasicSaveObject(), with a few exceptions:
	 *    1. There is no version int at the beginning.
	 *    2. Strings that are actually names have an extra int after the name.
	 *    3. For object, interface, and delegate properties in structs/objects, the value is an int that
	 *       is an index into the object list, instead of a string containing the object name. The value
	 *       -1 indicates that the reference was None or that the referenced object was not saved.
	 * 
	 * For delta objects, properties are only serialized if they have changed since the last state that the object was part of.
	 * This behavior is recursive for structs (and static arrays of structs) within the object:
	 * only the properties of the struct that have changed will be serialized.
	 * This behavior is not recursive for dynamic arrays or untyped properties.
	 */
	
	/**
	 * Decompresses a history file. The resulting file can be read by {@link #buildIndex(FileChannel)}.
	 * @param compressedIn The compressed (input) file
	 * @param decompressedOut The decompressed (output) file
	 * @throws IOException If there is a problem decompressing the file
	 */
	public void decompress(FileChannel compressedIn, FileChannel decompressedOut) throws IOException {
		decompressedOut.position(0);
		decompressedOut.truncate(0);
		
		long compressedFileSize = compressedIn.size();
		while (compressedIn.position() < compressedFileSize) {
			compressedIn.read(blockHeaderBuffer.position(0));
			
			if (blockHeaderBuffer.getInt(0) != UnrealUtils.UNREAL_MAGIC_NUMBER) {
				throw new IOException("Incorrect magic number for a compressed block");
			}
			
			compressedBlockBuffer = prepareBuffer(compressedBlockBuffer, blockHeaderBuffer.getInt(8));
			compressedIn.read(compressedBlockBuffer);
			compressedBlockBuffer.flip();
			
			uncompressedBlockBuffer = prepareBuffer(uncompressedBlockBuffer, blockHeaderBuffer.getInt(12));
			lzoSize.value = uncompressedBlockBuffer.remaining();
			int lzoResult = LzoDecompressor1x_safe.decompress(
					compressedBlockBuffer.array(),
					compressedBlockBuffer.arrayOffset() + compressedBlockBuffer.position(),
					compressedBlockBuffer.remaining(),
					uncompressedBlockBuffer.array(),
					uncompressedBlockBuffer.arrayOffset() + uncompressedBlockBuffer.position(),
					lzoSize,
					null);
			if (lzoResult != LzoTransformer.LZO_E_OK) {
				throw new IOException("LZO decompression failed with code " + lzoResult);
			}
			
			decompressedOut.write(uncompressedBlockBuffer);
		}
	}
	
	/**
	 * Builds an index of objects in a history file that has already been decompressed by
	 * {@link #decompress(FileChannel, FileChannel)}. The index can then be used to parse the objects.<br>
	 * <br>
	 * The returned index holds a reference to the history file, so the index must be closed when it is no longer needed.
	 * @param decompressedIn A channel to a file containing decompressed data. The caller is responsible for closing this.
	 * @return An index of objects in the history file. The caller is responsible for closing this.
	 * @throws IOException If there is a problem reading the history
	 */
	public X2HistoryIndex buildIndex(FileChannel decompressedIn) throws IOException {
		try {
			// check ArchiveFileLicenseeVersion to determine if this file was created by WOTC or not
			decompressedIn.position(4);
			decompressedIn.read(intBuffer.position(0));
			boolean createdByWOTC = intBuffer.getInt(0) == 120;
			
			// determine how many objects were written
			decompressedIn.read(intBuffer.position(0));
			int objectCount = intBuffer.getInt(0);
			
			List<X2HistoryIndexEntry> entries = new ArrayList<>(objectCount);
			
			// read the type strings
			uncompressedBlockBuffer.position(0).limit(0);
			for (int i = 0; i < objectCount; i++) {
				if (uncompressedBlockBuffer.remaining() < 1024) { // surely no class name will be >= 1024 characters right?
					decompressedIn.read(uncompressedBlockBuffer.compact());
					uncompressedBlockBuffer.flip();
				}
				entries.add(new X2HistoryIndexEntry(
						i,
						new UnrealName(UnrealUtils.readString(uncompressedBlockBuffer)),
						uncompressedBlockBuffer.position(uncompressedBlockBuffer.position() + 12).getInt()));
			}
			
			// reposition at the start of the first object
			decompressedIn.position(decompressedIn.position() - uncompressedBlockBuffer.remaining());
			
			// determine position and length for each entry
			for (var entry : entries) {
				decompressedIn.read(intBuffer.position(0));
				int length = intBuffer.getInt(0);
				long position = decompressedIn.position();
				entry.setPosition(position);
				entry.setLength(length);
				decompressedIn.position(position + length);
			}
			
			// use base typings to read the campaign settings, which contains the DLC/mod names
			// use those to figure out the correct typings
			// since the object will be delta'd, we have to look at all of them in order
			var mapper = new UnrealObjectMapper(new UnrealObjectParser(true, new UnrealTypingsBuilder().build(Set.of())));
			XComGameState_CampaignSettings campaignSettings = null;
			for (var entry : entries) {
				if (!entry.getType().equals(CAMPAIGN_SETTINGS_NAME)) {
					continue;
				}
				
				decompressedIn.position(entry.getPosition());
				var buffer = ByteBuffer.allocate(entry.getLength());
				decompressedIn.read(buffer);
				buffer.flip();
				
				if (campaignSettings == null) {
					campaignSettings = mapper.create(XComGameState_CampaignSettings.class, buffer, NullXComObjectReferenceResolver.INSTANCE);
				} else {
					campaignSettings = mapper.update(campaignSettings, buffer, NullXComObjectReferenceResolver.INSTANCE);
				}
			}
			
			// compute actual typings and update entries with their mapped types
			var typings = new UnrealTypingsBuilder().build(campaignSettings.RequiredDLC);
			for (var entry : entries) {
				var entryTyping = typings.getOrDefault(entry.getType(), UnrealTypeInformer.UNKNOWN);
				entry.setMappedType(entryTyping.mappedType);
				entry.setSingletonState(entryTyping.isSingletonStateType);
			}
			
			return new X2HistoryIndex(decompressedIn, createdByWOTC, entries, typings);
		} catch (IOException | RuntimeException e) {
			decompressedIn.close();
			throw e;
		}
	}
	
	private static ByteBuffer prepareBuffer(ByteBuffer buffer, int capacity) {
		if (buffer == null || buffer.capacity() < capacity) {
			buffer = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
		}
		return buffer.position(0).limit(capacity);
	}
	
}
