package com.github.rcd47.x2data.lib.savegame;

import java.time.LocalDateTime;
import java.util.List;

public class X2SaveGameHeader {
	
	public X2GameVersion gameVersion;
	public int campaignNumber;
	public int saveSlot;
	public String description;
	public LocalDateTime creationTime;
	public String mapCommand;
	public boolean tacticalSave;
	public boolean ironmanEnabled;
	public boolean autoSave;
	public String language;
	public LocalDateTime campaignStartTime;
	public String mapImage;
	public String name;
	public List<X2DlcNamePair> installedDlcAndMods;
	public int missionNumber;
	public int campaignMonth;
	public int tacticalTurn;
	public int tacticalAction;
	public String missionType;
	public int historyPosition;
	
}
