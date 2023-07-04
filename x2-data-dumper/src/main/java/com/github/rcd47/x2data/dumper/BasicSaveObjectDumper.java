package com.github.rcd47.x2data.dumper;

import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Set;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;

import j2html.tags.specialized.BodyTag;

public class BasicSaveObjectDumper {
	
	private static final UnrealName OBJECT_ID = new UnrealName("ObjectID");
	
	public void dumpObject(FileChannel in, BodyTag body) throws IOException {
		var buffer = ByteBuffer.allocate((int) in.size());
		in.read(buffer);
		
		// note that we do not know the object type or which DLCs/mods are installed here
		// TODO establish a standard file naming convention to handle the former
		// TODO let the user specify a companion save/history file so we can determine the DLCs to handle the latter
		var parser = new UnrealObjectParser(false, new UnrealTypingsBuilder().build(Set.of()));
		var visitor = new GenericObjectVisitor(null);
		parser.parse(new UnrealName("UNKNOWN"), buffer.flip(), visitor);
		
		var objectTableBody = tbody();
		if (visitor.rootObject.properties.containsKey(OBJECT_ID)) {
			body.with(table(thead(tr(th("Object ID"), th("Path"), th("Value"))), objectTableBody).withClasses("table", "table-sm"));
		} else {
			body.with(table(thead(tr(th("Path"), th("Value"))), objectTableBody).withClasses("table", "table-sm"));
		}
		GenericObject.dump(objectTableBody, visitor.rootObject);
	}
	
}
