package com.github.rcd47.x2data.lib.history;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.anarres.lzo.LzoDecompressor1x_safe;
import org.anarres.lzo.LzoTransformer;
import org.anarres.lzo.lzo_uintp;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.UnrealUtils;
import com.github.rcd47.x2data.lib.unreal.mapper.UnrealObjectMapper;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState_CampaignSettings;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypeInformer;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;
import com.google.common.flogger.FluentLogger;

public class X2HistoryReader {
	
	private static final FluentLogger L = FluentLogger.forEnclosingClass();
	
	private Set<OpenOption> decompressOpenOptions;
	private ByteBuffer intBuffer;
	private ByteBuffer blockHeaderBuffer;
	private ByteBuffer compressedBlockBuffer;
	private ByteBuffer uncompressedBlockBuffer;
	private lzo_uintp lzoSize;
	
	public X2HistoryReader() {
		decompressOpenOptions = Set.of(
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.READ, StandardOpenOption.WRITE);
		intBuffer = prepareBuffer(null, 4);
		blockHeaderBuffer = prepareBuffer(null, 24);
		compressedBlockBuffer = prepareBuffer(null, 131_072);
		uncompressedBlockBuffer = prepareBuffer(null, 131_072);
		lzoSize = new lzo_uintp();
	}

	/**
	 * Builds an index of objects in a history file. The index can then be used to parse the objects.<br>
	 * <br>
	 * This method decompresses the history first and creates a temp file containing the decompressed data.
	 * The returned index holds a reference to that file, so the index must be closed when it is no longer needed.
	 * @param compressedIn A channel to a file containing compressed data. The caller is responsible for closing this.
	 * @return An index of objects in the history file. The caller is responsible for closing this.
	 * @throws IOException If there is a problem reading the history
	 */
	public X2HistoryIndex buildIndex(FileChannel compressedIn) throws IOException {
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
		 * 2. int: ArchiveFileLicenseeVersion. Always 120.
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
		
		Path decompressedTempFile = Files.createTempFile("x2hist-decompress-", null);
		FileChannel decompressedOut = FileChannel.open(decompressedTempFile, decompressOpenOptions);
		L.atFine().log("Decompressed data is being written to temp file %s", decompressedTempFile);
		
		try {
			// decompress the data
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
			
			// determine how many objects were written
			decompressedOut.position(8);
			decompressedOut.read(intBuffer.position(0));
			int objectCount = intBuffer.getInt(0);
			
			List<X2HistoryIndexEntry> entries = new ArrayList<>(objectCount);
			
			// read the type strings
			uncompressedBlockBuffer.position(0).limit(0);
			for (int i = 0; i < objectCount; i++) {
				if (uncompressedBlockBuffer.remaining() < 1024) { // surely no class name will be >= 1024 characters right?
					decompressedOut.read(uncompressedBlockBuffer.compact());
					uncompressedBlockBuffer.flip();
				}
				entries.add(new X2HistoryIndexEntry(
						i,
						UnrealUtils.readString(uncompressedBlockBuffer),
						uncompressedBlockBuffer.position(uncompressedBlockBuffer.position() + 12).getInt()));
			}
			
			// reposition at the start of the first object
			decompressedOut.position(decompressedOut.position() - uncompressedBlockBuffer.remaining());
			
			// determine position and length for each entry
			for (var entry : entries) {
				decompressedOut.read(intBuffer.position(0));
				int length = intBuffer.getInt(0);
				long position = decompressedOut.position();
				entry.setPosition(position);
				entry.setLength(length);
				decompressedOut.position(position + length);
			}
			
			// use base typings to read the campaign settings, which contains the DLC/mod names
			// use those to figure out the correct typings
			// since the object will be delta'd, we have to look at all of them in order
			var mapper = new UnrealObjectMapper(new UnrealObjectParser(true, new UnrealTypingsBuilder().build(Set.of())));
			XComGameState_CampaignSettings campaignSettings = null;
			for (var entry : entries) {
				if (!entry.getType().equals("XComGameState_CampaignSettings")) {
					continue;
				}
				
				decompressedOut.position(entry.getPosition());
				var buffer = ByteBuffer.allocate(entry.getLength());
				decompressedOut.read(buffer);
				buffer.flip();
				
				if (campaignSettings == null) {
					campaignSettings = mapper.create(XComGameState_CampaignSettings.class, buffer);
				} else {
					campaignSettings = mapper.update(campaignSettings, buffer);
				}
			}
			
			// compute actual typings and update entries with their mapped types
			var typings = new UnrealTypingsBuilder().build(campaignSettings.RequiredDLC);
			for (var entry : entries) {
				var entryTyping = typings.getOrDefault(entry.getType(), UnrealTypeInformer.UNKNOWN);
				entry.setMappedType(entryTyping.mappedType);
				entry.setSingletonState(entryTyping.isSingletonStateType);
			}
			
			return new X2HistoryIndex(decompressedOut, entries, typings);
		} catch (IOException | RuntimeException e) {
			decompressedOut.close();
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
