package com.github.rcd47.x2data.dumper;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.li;
import static j2html.TagCreator.span;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;
import static j2html.TagCreator.ul;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.rcd47.x2data.lib.history.X2HistoryIndex;
import com.github.rcd47.x2data.lib.history.X2HistoryIndexEntry;
import com.github.rcd47.x2data.lib.history.X2HistoryReader;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import j2html.tags.specialized.BodyTag;

public class HistoryFileDumper {
	
	public void dumpHistory(FileChannel in, BodyTag body, boolean onlyChangedProperties, String filter) throws IOException {
		X2HistoryIndex historyIndex = new X2HistoryReader().buildIndex(in);
		
		XComGameStateHistory history = historyIndex.mapObject(historyIndex.getEntry(0), null);
		
		body.with(h1("History"));
		
		Script filterScript = null;
		if (filter != null) {
			filterScript = new GroovyShell().parse(filter);
			body.with(div("Filter: " + filter));
		}
		
		body.with(div("Current random seed: " + history.CurrRandomSeed));
		body.with(div("Frames " + history.NumArchivedFrames + " to " + (history.NumArchivedFrames + history.History.size() - 1)));
		
		var frameLinks = ul(li(a("Singleton State Objects").withHref("#singletonStateObjects")));
		body.with(frameLinks);
		
		var singletonStateTableBody = tbody();
		body.with(
				h3("Singleton State Objects").withId("singletonStateObjects"),
				table(thead(tr(th("Object ID"), th("Path"), th("Value"))), singletonStateTableBody).withClasses("table", "table-sm"));
		
		Map<Integer, GenericObject> parsedObjects = new HashMap<>();
		Set<X2HistoryIndexEntry> singletonStates = new HashSet<>();
		boolean firstFrame = true;
		for (int frameIndex : history.History) {
			XComGameState frame = historyIndex.mapObject(historyIndex.getEntry(frameIndex), null);
			
			var contextEntry = historyIndex.getEntry(frame.StateChangeContext);
			var contextVisitor = new GenericObjectVisitor(null);
			historyIndex.parseObject(contextEntry, contextVisitor);
			
			var objectsTableBody = tbody();
			List<GenericObject> parsedObjectsThisState = new ArrayList<>(frame.GameStates.size());
			for (int stateObjectIndex : frame.GameStates) {
				var stateObjectEntry = historyIndex.getEntry(stateObjectIndex);
				if (stateObjectEntry.isSingletonState()) {
					singletonStates.add(stateObjectEntry);
					continue;
				}
				
				var previousVersionIndex = stateObjectEntry.getPreviousVersionIndex();
				var previousVersion = previousVersionIndex == -1 ? null : parsedObjects.get(previousVersionIndex);
				var stateObjectVisitor = new GenericObjectVisitor(previousVersion);
				historyIndex.parseObject(stateObjectEntry, stateObjectVisitor);
				parsedObjects.put(stateObjectIndex, stateObjectVisitor.rootObject);
				parsedObjectsThisState.add(stateObjectVisitor.rootObject);
				
				if (Boolean.TRUE.equals(stateObjectVisitor.rootObject.properties.get("bRemoved")) && !firstFrame) {
					GenericObject.dump(objectsTableBody, previousVersion, null, onlyChangedProperties);
				} else if (firstFrame) {
					GenericObject.dump(objectsTableBody, stateObjectVisitor.rootObject);
				} else {
					GenericObject.dump(objectsTableBody, previousVersion, stateObjectVisitor.rootObject, onlyChangedProperties);
				}
			}
			
			if (filterScript != null) {
				var scriptBinding = new Binding();
				scriptBinding.setVariable("historyIndex", frame.HistoryIndex);
				scriptBinding.setVariable("context", contextVisitor.rootObject);
				scriptBinding.setVariable("states", parsedObjectsThisState);
				filterScript.setBinding(scriptBinding);
				if (!((boolean) filterScript.run())) {
					firstFrame = false;
					continue;
				}
			}
			
			var contextTableBody = tbody();
			GenericObject.dump(contextTableBody, contextVisitor.rootObject);
			
			var frameLinkText = "#" + frame.HistoryIndex + " - " + contextVisitor.rootObject.type;
			if ("XComGameStateContext_ChangeContainer".equalsIgnoreCase(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get("ChangeInfo");
			} else if ("XComGameStateContext_StrategyGameRule".equalsIgnoreCase(contextVisitor.rootObject.type) ||
					"XComGameStateContext_TacticalGameRule".equalsIgnoreCase(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get("GameRuleType");
			} else if ("XComGameStateContext_Ability".equalsIgnoreCase(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + ((GenericObject) contextVisitor.rootObject.properties.get("InputContext")).properties.get("AbilityTemplateName");
			} else if ("XComGameStateContext_WillRoll".equalsIgnoreCase(contextVisitor.rootObject.type)) {
				frameLinkText += ": Unit " + contextVisitor.rootObject.properties.get("TargetUnitID");
			} else if ("XComGameStateContext_Kismet".equalsIgnoreCase(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get("SeqOpName");
			}
			
			var frameLink = li(a(frameLinkText).withHref("#frame" + frame.HistoryIndex));
			frameLinks.with(frameLink);
			
			var interruptedAtIndex = contextVisitor.rootObject.properties.get("InterruptionHistoryIndex");
			if (interruptedAtIndex != null) {
				frameLink.with(span(" [RESUMED - interrupted at " + interruptedAtIndex + "]"));
			}
			
			var resumeIndex = contextVisitor.rootObject.properties.get("ResumeHistoryIndex");
			if (resumeIndex != null) {
				frameLink.with(span(" [INTERRUPTED - resumes at " + resumeIndex + "]"));
			}
			
			var interruptingIndex = contextVisitor.rootObject.properties.get("HistoryIndexInterruptedBySelf");
			if (interruptingIndex != null) {
				frameLink.with(span(" [INTERRUPTS " + interruptingIndex + "]"));
			}
			
			body.with(
					h2("Frame " + frame.HistoryIndex + " at " + frame.TimeStamp).withId("frame" + frame.HistoryIndex),
					h3("Context"),
					table(thead(tr(th("Path"), th("Value"))), contextTableBody).withClasses("table", "table-sm"));
			
			body.with(h3("State Objects"));
			if (firstFrame) {
				body.with(table(thead(tr(th("Object ID"), th("Path"), th("Value"))), objectsTableBody).withClasses("table", "table-sm"));
			} else {
				body.with(table(thead(tr(th("Object ID"), th("Path"), th("Old Value"), th("New Value"))), objectsTableBody).withClasses("table", "table-sm"));
			}
			
			firstFrame = false;
		}
		
		singletonStates.stream().sorted((a, b) -> a.getType().compareTo(b.getType())).forEach(singletonState -> {
			var stateObjectVisitor = new GenericObjectVisitor(null);
			try {
				historyIndex.parseObject(singletonState, stateObjectVisitor);
			} catch (IOException e) {
				// should never happen
				throw new UncheckedIOException(e);
			}
			GenericObject.dump(singletonStateTableBody, stateObjectVisitor.rootObject);
		});
	}
	
}
