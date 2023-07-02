package com.github.rcd47.x2data.dumper;

import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.li;
import static j2html.TagCreator.ul;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.format.DateTimeFormatter;

import com.github.rcd47.x2data.lib.savegame.X2GameVersion;
import com.github.rcd47.x2data.lib.savegame.X2SaveGameReader;

import j2html.tags.specialized.BodyTag;

public class SaveHeaderDumper {
	
	public void dumpHeader(FileChannel in, BodyTag body) throws IOException {
		var header = new X2SaveGameReader().readHeader(in);
		var dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		body.with(h1("Header"));
		body.with(div("Game version: " + header.gameVersion));
		body.with(div("Save name: " + header.name));
		body.with(div("Description: " + header.description));
		body.with(div("Is tactical save: " + header.tacticalSave));
		body.with(div("Is auto save: " + header.autoSave));
		body.with(div("Is Ironman enabled: " + header.ironmanEnabled));
		body.with(div("Language: " + header.language));
		body.with(div("Campaign start time: " + header.campaignStartTime.format(dateTimeFormat)));
		body.with(div("File creation time: " + header.creationTime.format(dateTimeFormat)));
		body.with(div("Map command: " + header.mapCommand));
		body.with(div("Map image: " + header.mapImage));
		body.with(div("Save slot: " + header.saveSlot));
		body.with(div("Campaign number: " + header.campaignNumber));
		
		if (header.gameVersion != X2GameVersion.XCOM2) {
			body.with(div("Campaign month: " + header.campaignMonth));
			body.with(div("Mission number: " + header.missionNumber));
			body.with(div("Mission type: " + header.missionType));
			body.with(div("Tactical turn: " + header.tacticalTurn));
			body.with(div("Tactical action: " + header.tacticalAction));
		}
		
		var dlcList = ul();
		body.with(div("Installed DLCs/mods:"), dlcList);
		for (var dlc : header.installedDlcAndMods) {
			dlcList.with(li(dlc.friendlyName + " (" + dlc.internalName + ")"));
		}
	}
	
}
