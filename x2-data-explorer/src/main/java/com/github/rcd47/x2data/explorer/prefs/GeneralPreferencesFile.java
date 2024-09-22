package com.github.rcd47.x2data.explorer.prefs;

import java.util.List;
import java.util.Set;

public class GeneralPreferencesFile {

	private int fontSize;
	private HistoryFileTab historyFileDefaultTab;
	private HistoryFileTab saveFileDefaultTab;
	private boolean bsoTreeExpanded;
	private boolean historyObjPropsTreeExpanded;
	private boolean historySingletonPropsTreeExpanded;
	private boolean historyContextPropsTreeExpanded;
	private List<HistoryFramesColumn> framesColumnOrder;
	private Set<HistoryFramesColumn> framesColumnVisible;
	private List<HistoryObjectsColumn> objectsColumnOrder;
	private Set<HistoryObjectsColumn> objectsColumnVisible;
	private List<ObjectPropertiesColumn> objPropsColumnOrder;
	private Set<ObjectPropertiesColumn> objPropsColumnVisible;

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public HistoryFileTab getHistoryFileDefaultTab() {
		return historyFileDefaultTab;
	}

	public HistoryFileTab getSaveFileDefaultTab() {
		return saveFileDefaultTab;
	}

	public void setSaveFileDefaultTab(HistoryFileTab saveFileDefaultTab) {
		this.saveFileDefaultTab = saveFileDefaultTab;
	}

	public void setHistoryFileDefaultTab(HistoryFileTab historyFileDefaultTab) {
		this.historyFileDefaultTab = historyFileDefaultTab;
	}

	public boolean isBsoTreeExpanded() {
		return bsoTreeExpanded;
	}

	public void setBsoTreeExpanded(boolean bsoTreeExpanded) {
		this.bsoTreeExpanded = bsoTreeExpanded;
	}

	public boolean isHistoryObjPropsTreeExpanded() {
		return historyObjPropsTreeExpanded;
	}

	public void setHistoryObjPropsTreeExpanded(boolean historyObjPropsTreeExpanded) {
		this.historyObjPropsTreeExpanded = historyObjPropsTreeExpanded;
	}

	public boolean isHistorySingletonPropsTreeExpanded() {
		return historySingletonPropsTreeExpanded;
	}

	public void setHistorySingletonPropsTreeExpanded(boolean historySingletonPropsTreeExpanded) {
		this.historySingletonPropsTreeExpanded = historySingletonPropsTreeExpanded;
	}

	public boolean isHistoryContextPropsTreeExpanded() {
		return historyContextPropsTreeExpanded;
	}

	public void setHistoryContextPropsTreeExpanded(boolean historyContextPropsTreeExpanded) {
		this.historyContextPropsTreeExpanded = historyContextPropsTreeExpanded;
	}

	public List<HistoryFramesColumn> getFramesColumnOrder() {
		return framesColumnOrder;
	}

	public void setFramesColumnOrder(List<HistoryFramesColumn> framesColumnOrder) {
		this.framesColumnOrder = framesColumnOrder;
	}

	public Set<HistoryFramesColumn> getFramesColumnVisible() {
		return framesColumnVisible;
	}

	public void setFramesColumnVisible(Set<HistoryFramesColumn> framesColumnVisible) {
		this.framesColumnVisible = framesColumnVisible;
	}

	public List<HistoryObjectsColumn> getObjectsColumnOrder() {
		return objectsColumnOrder;
	}

	public void setObjectsColumnOrder(List<HistoryObjectsColumn> objectsColumnOrder) {
		this.objectsColumnOrder = objectsColumnOrder;
	}

	public Set<HistoryObjectsColumn> getObjectsColumnVisible() {
		return objectsColumnVisible;
	}

	public void setObjectsColumnVisible(Set<HistoryObjectsColumn> objectsColumnVisible) {
		this.objectsColumnVisible = objectsColumnVisible;
	}

	public List<ObjectPropertiesColumn> getObjPropsColumnOrder() {
		return objPropsColumnOrder;
	}

	public void setObjPropsColumnOrder(List<ObjectPropertiesColumn> objPropsColumnOrder) {
		this.objPropsColumnOrder = objPropsColumnOrder;
	}

	public Set<ObjectPropertiesColumn> getObjPropsColumnVisible() {
		return objPropsColumnVisible;
	}

	public void setObjPropsColumnVisible(Set<ObjectPropertiesColumn> objPropsColumnVisible) {
		this.objPropsColumnVisible = objPropsColumnVisible;
	}
	
}
