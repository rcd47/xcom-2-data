package com.github.rcd47.x2data.lib.history;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.IUnrealObjectVisitor;
import com.github.rcd47.x2data.lib.unreal.UnrealFileParseException;
import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.mapper.UnrealObjectMapper;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypeInformer;

public class X2HistoryIndex implements Closeable {
	
	private FileChannel file;
	private List<X2HistoryIndexEntry> entries;
	private Map<String, UnrealTypeInformer> typings;
	private UnrealObjectParser objectParser;
	private UnrealObjectMapper objectMapper;
	private int largestEntrySize;
	private Deque<ByteBuffer> bufferCache;
	
	X2HistoryIndex(FileChannel file, List<X2HistoryIndexEntry> entries, Map<String, UnrealTypeInformer> typings) {
		this.file = file;
		this.entries = entries;
		this.typings = typings;
		objectParser = new UnrealObjectParser(true, typings);
		objectMapper = new UnrealObjectMapper(objectParser);
		largestEntrySize = entries.stream().mapToInt(X2HistoryIndexEntry::getLength).max().getAsInt();
		bufferCache = new ArrayDeque<>();
	}
	
	public X2HistoryIndexEntry getEntry(int index) {
		return entries.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T mapObject(X2HistoryIndexEntry entry, T previousVersion) throws IOException {
		var buffer = prepareBuffer(entry);
		try {
			return previousVersion == null ?
					(T) objectMapper.create(entry.getMappedType(), buffer) : objectMapper.update(previousVersion, buffer);
		} catch (Exception e) {
			throw buildParseException(entry, e);
		} finally {
			bufferCache.offerFirst(buffer);
		}
	}
	
	public void parseObject(X2HistoryIndexEntry entry, IUnrealObjectVisitor visitor) throws IOException {
		var buffer = prepareBuffer(entry);
		try {
			objectParser.parse(entry.getType(), buffer, visitor);
		} catch (Exception e) {
			throw buildParseException(entry, e);
		} finally {
			bufferCache.offerFirst(buffer);
		}
	}
	
	private UnrealFileParseException buildParseException(X2HistoryIndexEntry entry, Exception e) {
		return new UnrealFileParseException(
				"Entry index " + entry.getArrayIndex() + " with type " + entry.getType() + " and length " + entry.getLength(),
				e, entry.getPosition());
	}
	
	private ByteBuffer prepareBuffer(X2HistoryIndexEntry entry) throws IOException {
		int arrayIndex = entry.getArrayIndex();
		if (arrayIndex >= entries.size() || entries.get(arrayIndex) != entry) {
			throw new IllegalArgumentException("Entry does not belong to this index");
		}
		
		var buffer = bufferCache.pollFirst();
		if (buffer == null) {
			buffer = ByteBuffer.allocate(largestEntrySize).order(ByteOrder.LITTLE_ENDIAN);
		}
		buffer.position(0).limit(entry.getLength());
		
		file.position(entry.getPosition());
		file.read(buffer);
		
		return buffer.flip();
	}

	@Override
	public void close() throws IOException {
		file.close();
		file = null;
	}

	public Map<String, UnrealTypeInformer> getTypings() {
		return typings;
	}

	// only exists for unit tests
	List<X2HistoryIndexEntry> getEntries() {
		return entries;
	}
	
}
