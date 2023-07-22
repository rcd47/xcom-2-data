package com.github.rcd47.x2data.dumper;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h4;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.title;

import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.Callable;

import com.github.rcd47.x2data.lib.unreal.UnrealUtils;

import j2html.rendering.IndentedHtml;
import j2html.tags.specialized.BodyTag;
import j2html.tags.specialized.HeadTag;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "x2dd", mixinStandardHelpOptions = true, showAtFileInUsageHelp = true, versionProvider = CliVersionProvider.class)
public class Main implements Callable<Integer> {
	
	@Parameters(index = "0", description = "Location of the file to parse.")
	private Path inputFile;
	@Parameters(index = "1", description = "Location to write the output.")
	private Path outputFile;
	@Option(names = {"--inputFileType"}, description = "What type of file is being parsed. Auto-detected if not specified.")
	private FileType inputFileType;
	@Option(names = {"-a", "--dumpAllProperties"}, description = "Dump all properties in a changed object instead of just the properties that changed.")
	private boolean dumpAllProperties;
	@Option(names = {"-f", "--historyFilter"},
			description = {
					"When dumping history, only frames that match this filter are dumped.",
					"By default, there is no filter, and all frames will be dumped.",
					"However, that tends to produce output large enough to make your browser cry.",
					"The filter is a Groovy expression that must return a boolean value.",
					"The variables available to you are:",
					"historyIndex - the index of the history frame",
					"context - the history frame's XComGameStateContext",
					"states - a List of the history frame's XComGameState objects",
					"Examples:",
					"Match frames by index: historyIndex >= 9450 && historyIndex <= 9630",
					"Match single-target abilities against a given target: context.InputContext?.PrimaryTarget?.ObjectID = 2315"
			})
	private String historyFilter;
	@Option(names = {"-d", "--decompressedFile"},
			description = {
					"When dumping history, save the decompressed file here.",
					"If not specified, a temporary file will be used and deleted afterwards."
			})
	private Path decompressedFile;
	
	@Override
	public Integer call() throws Exception {
		try (FileChannel in = FileChannel.open(inputFile, Set.of(StandardOpenOption.READ));
				Writer out = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
			if (inputFileType == null) {
				ByteBuffer typeDetectBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
				in.read(typeDetectBuffer, 0);
				
				/*
				 * The second int is:
				 * - header size, for save files
				 * - max block size, for history files
				 * - -1, for BasicSaveObject files
				 * So if the second int is -1, we assume the file format is BasicSaveObject since sizes should never be negative.
				 * Otherwise, we check the first int. The first int is:
				 * - save version, for save files (0x14, 0x15, or 0x16 depending on the version of XCOM 2)
				 * - unreal magic number, for history files
				 * - version passed to BasicSaveObject, for BasicSaveObject files
				 * So if the first int is the unreal magic number, we assume the file format is history.
				 */
				if (typeDetectBuffer.getInt(4) == -1) {
					inputFileType = FileType.BASIC_SAVE_OBJECT;
				} else if (typeDetectBuffer.getInt(0) == UnrealUtils.UNREAL_MAGIC_NUMBER) {
					inputFileType = FileType.HISTORY;
				} else {
					inputFileType = FileType.GAME_SAVE;
				}
				System.out.println("Detected file type " + inputFileType);
			}
			
			String title = "Dump of " + inputFile;
			BodyTag body = body(h1(title), h4("Created by x2dd version " + CliVersionProvider.VERSION).withClass("text-info"));
			switch (inputFileType) {
				case BASIC_SAVE_OBJECT:
					new BasicSaveObjectDumper().dumpObject(in, body);
					break;
				case GAME_SAVE:
					new SaveHeaderDumper().dumpHeader(in, body);
					// deliberate fall-through
				case HISTORY:
					boolean decompressedFileIsTemp = decompressedFile == null;
					if (decompressedFileIsTemp) {
						decompressedFile = Files.createTempFile("x2hist-decompress-", null);
					}
					try {
						new HistoryFileDumper().dumpHistory(in, decompressedFile, body, !dumpAllProperties, historyFilter);
					} finally {
						if (decompressedFileIsTemp) {
							Files.deleteIfExists(decompressedFile);
						}
					}
					break;
				default:
					throw new IllegalArgumentException("Unsupported file type " + inputFileType);
			}
			
			HeadTag head = head(
					title(title),
					meta().withCharset("UTF-8"),
					link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"));
			html(head, body).render(IndentedHtml.into(out));
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		System.exit(new CommandLine(new Main()).execute(args));
	}
	
	private static enum FileType {
		GAME_SAVE, HISTORY, BASIC_SAVE_OBJECT
	}
	
}
