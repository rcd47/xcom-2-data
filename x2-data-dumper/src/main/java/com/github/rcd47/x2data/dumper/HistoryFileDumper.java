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
import com.github.rcd47.x2data.lib.unreal.mapper.ref.NullXComObjectReferenceResolver;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameState;
import com.github.rcd47.x2data.lib.unreal.mappings.base.XComGameStateHistory;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import j2html.tags.specialized.BodyTag;

public class HistoryFileDumper {
	
	private static final UnrealName CONTEXT_CHANGE_CONTAINER = new UnrealName("XComGameStateContext_ChangeContainer");
	private static final UnrealName CONTEXT_STRATEGY_RULE = new UnrealName("XComGameStateContext_StrategyGameRule");
	private static final UnrealName CONTEXT_TACTICAL_RULE = new UnrealName("XComGameStateContext_TacticalGameRule");
	private static final UnrealName CONTEXT_ABILITY = new UnrealName("XComGameStateContext_Ability");
	private static final UnrealName CONTEXT_WILL_ROLL = new UnrealName("XComGameStateContext_WillRoll");
	private static final UnrealName CONTEXT_KISMET = new UnrealName("XComGameStateContext_Kismet");
	private static final UnrealName REMOVED = new UnrealName("bRemoved");
	private static final UnrealName CHANGE_INFO = new UnrealName("ChangeInfo");
	private static final UnrealName GAME_RULE_TYPE = new UnrealName("GameRuleType");
	private static final UnrealName INPUT_CONTEXT = new UnrealName("InputContext");
	private static final UnrealName ABILITY_TEMPLATE_NAME = new UnrealName("AbilityTemplateName");
	private static final UnrealName TARGET_UNIT_ID = new UnrealName("TargetUnitID");
	private static final UnrealName SEQ_OP_NAME = new UnrealName("SeqOpName");
	private static final UnrealName INTERRUPT_INDEX = new UnrealName("InterruptionHistoryIndex");
	private static final UnrealName RESUME_INDEX = new UnrealName("ResumeHistoryIndex");
	private static final UnrealName INTERRUPTED_SELF = new UnrealName("HistoryIndexInterruptedBySelf");
	
	public void dumpHistory(FileChannel in, BodyTag body, boolean onlyChangedProperties, String filter) throws IOException {
		X2HistoryIndex historyIndex = new X2HistoryReader().buildIndex(in);
		
		XComGameStateHistory history = historyIndex.mapObject(historyIndex.getEntry(0), null, NullXComObjectReferenceResolver.INSTANCE);
		
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
		for (var frameRef : history.History) {
			XComGameState frame = historyIndex.mapObject(historyIndex.getEntry(frameRef.index()), null, NullXComObjectReferenceResolver.INSTANCE);
			
			var contextEntry = historyIndex.getEntry(frame.StateChangeContext.index());
			var contextVisitor = new GenericObjectVisitor(null);
			historyIndex.parseObject(contextEntry, contextVisitor);
			
			var objectsTableBody = tbody();
			List<GenericObject> parsedObjectsThisState = new ArrayList<>(frame.GameStates.size());
			for (var stateObjectRef : frame.GameStates) {
				var stateObjectEntry = historyIndex.getEntry(stateObjectRef.index());
				if (stateObjectEntry.isSingletonState()) {
					singletonStates.add(stateObjectEntry);
					continue;
				}
				
				var previousVersionIndex = stateObjectEntry.getPreviousVersionIndex();
				var previousVersion = previousVersionIndex == -1 ? null : parsedObjects.get(previousVersionIndex);
				var stateObjectVisitor = new GenericObjectVisitor(previousVersion);
				historyIndex.parseObject(stateObjectEntry, stateObjectVisitor);
				parsedObjects.put(stateObjectRef.index(), stateObjectVisitor.rootObject);
				parsedObjectsThisState.add(stateObjectVisitor.rootObject);
				
				if (Boolean.TRUE.equals(stateObjectVisitor.rootObject.properties.get(REMOVED)) && !firstFrame) {
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
			if (CONTEXT_CHANGE_CONTAINER.equals(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get(CHANGE_INFO);
			} else if (CONTEXT_STRATEGY_RULE.equals(contextVisitor.rootObject.type) ||
					CONTEXT_TACTICAL_RULE.equals(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get(GAME_RULE_TYPE);
			} else if (CONTEXT_ABILITY.equals(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + ((GenericObject) contextVisitor.rootObject.properties.get(INPUT_CONTEXT)).properties.get(ABILITY_TEMPLATE_NAME);
			} else if (CONTEXT_WILL_ROLL.equals(contextVisitor.rootObject.type)) {
				frameLinkText += ": Unit " + contextVisitor.rootObject.properties.get(TARGET_UNIT_ID);
			} else if (CONTEXT_KISMET.equals(contextVisitor.rootObject.type)) {
				frameLinkText += ": " + contextVisitor.rootObject.properties.get(SEQ_OP_NAME);
			}
			
			var frameLink = li(a(frameLinkText).withHref("#frame" + frame.HistoryIndex));
			frameLinks.with(frameLink);
			
			var interruptedAtIndex = contextVisitor.rootObject.properties.get(INTERRUPT_INDEX);
			if (interruptedAtIndex != null) {
				frameLink.with(span(" [RESUMED - interrupted at " + interruptedAtIndex + "]"));
			}
			
			var resumeIndex = contextVisitor.rootObject.properties.get(RESUME_INDEX);
			if (resumeIndex != null) {
				frameLink.with(span(" [INTERRUPTED - resumes at " + resumeIndex + "]"));
			}
			
			var interruptingIndex = contextVisitor.rootObject.properties.get(INTERRUPTED_SELF);
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
