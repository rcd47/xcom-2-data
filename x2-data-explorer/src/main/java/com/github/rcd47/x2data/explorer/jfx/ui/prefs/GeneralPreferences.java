package com.github.rcd47.x2data.explorer.jfx.ui.prefs;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rcd47.x2data.explorer.prefs.GeneralPreferencesFile;
import com.github.rcd47.x2data.explorer.prefs.HistoryFileTab;
import com.github.rcd47.x2data.explorer.prefs.HistoryFramesColumn;
import com.github.rcd47.x2data.explorer.prefs.HistoryObjectsColumn;
import com.github.rcd47.x2data.explorer.prefs.ObjectPropertiesColumn;
import com.github.rcd47.x2data.explorer.prefs.StoragePaths;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GeneralPreferences {
	
	private static final Logger L = LogManager.getLogger(GeneralPreferences.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final GeneralPreferences EFFECTIVE = loadFromFile();
	
	private final SimpleIntegerProperty fontSize;
	private final SimpleObjectProperty<HistoryFileTab> historyFileDefaultTab;
	private final SimpleObjectProperty<HistoryFileTab> saveFileDefaultTab;
	private final SimpleBooleanProperty bsoTreeExpanded;
	private final SimpleBooleanProperty historyObjPropsTreeExpanded;
	private final SimpleBooleanProperty historySingletonPropsTreeExpanded;
	private final SimpleBooleanProperty historyContextPropsTreeExpanded;
	private final ObservableList<TableColumnPreferences<HistoryFramesColumn>> framesColumns;
	private final ObservableList<TableColumnPreferences<HistoryObjectsColumn>> objectsColumns;
	private final ObservableList<TableColumnPreferences<ObjectPropertiesColumn>> objPropsColumns;
	
	private GeneralPreferences() {
		fontSize = new SimpleIntegerProperty();
		historyFileDefaultTab = new SimpleObjectProperty<>();
		saveFileDefaultTab = new SimpleObjectProperty<>();
		bsoTreeExpanded = new SimpleBooleanProperty();
		historyObjPropsTreeExpanded = new SimpleBooleanProperty();
		historySingletonPropsTreeExpanded = new SimpleBooleanProperty();
		historyContextPropsTreeExpanded = new SimpleBooleanProperty();
		framesColumns = FXCollections.observableArrayList();
		objectsColumns = FXCollections.observableArrayList();
		objPropsColumns = FXCollections.observableArrayList();
	}

	public void resetToDefaults() {
		fontSize.set(10);
		historyFileDefaultTab.set(HistoryFileTab.FRAMES);
		saveFileDefaultTab.set(HistoryFileTab.FRAMES);
		bsoTreeExpanded.set(true);
		historyObjPropsTreeExpanded.set(true);
		historySingletonPropsTreeExpanded.set(true);
		historyContextPropsTreeExpanded.set(true);
		resetToDefaults(HistoryFramesColumn.values(), framesColumns);
		resetToDefaults(HistoryObjectsColumn.values(), objectsColumns);
		resetToDefaults(ObjectPropertiesColumn.values(), objPropsColumns);
	}
	
	private <T> void resetToDefaults(T[] values, ObservableList<TableColumnPreferences<T>> columns) {
		// must not throw away the existing objects
		// if we do, we break the reset button on the prefs UI
		var resetColumns = Stream
				.of(values)
				.map(c -> columns
						.stream()
						.filter(p -> p.getColumn() == c)
						.findAny()
						.orElseGet(() -> new TableColumnPreferences<>(c, true)))
				.toList();
		for (var column : columns) {
			column.getVisible().set(true);
		}
		columns.clear();
		columns.addAll(resetColumns);
	}
	
	public void applyChanges() throws IOException {
		copyTo(EFFECTIVE);
		
		var defaultPrefs = new GeneralPreferences();
		defaultPrefs.resetToDefaults();
		if (fontSize.get() == defaultPrefs.fontSize.get() &&
				historyFileDefaultTab.get() == defaultPrefs.historyFileDefaultTab.get() &&
				saveFileDefaultTab.get() == defaultPrefs.saveFileDefaultTab.get() &&
				bsoTreeExpanded.get() == defaultPrefs.bsoTreeExpanded.get() &&
				historyObjPropsTreeExpanded.get() == defaultPrefs.historyObjPropsTreeExpanded.get() &&
				historySingletonPropsTreeExpanded.get() == defaultPrefs.historySingletonPropsTreeExpanded.get() &&
				historyContextPropsTreeExpanded.get() == defaultPrefs.historyContextPropsTreeExpanded.get() &&
				areColumnPrefsEqual(framesColumns, defaultPrefs.framesColumns) &&
				areColumnPrefsEqual(objectsColumns, defaultPrefs.objectsColumns) &&
				areColumnPrefsEqual(objPropsColumns, defaultPrefs.objPropsColumns)) {
			// settings were reset to default, so delete the file
			// that way, if we change the defaults in the future, the user automatically gets the new defaults
			Files.deleteIfExists(StoragePaths.GENERAL_PREFERENCES_FILE);
			return;
		}
		
		var file = new GeneralPreferencesFile();
		file.setFontSize(fontSize.get());
		file.setHistoryFileDefaultTab(historyFileDefaultTab.get());
		file.setSaveFileDefaultTab(saveFileDefaultTab.get());
		file.setBsoTreeExpanded(bsoTreeExpanded.get());
		file.setHistoryObjPropsTreeExpanded(historyObjPropsTreeExpanded.get());
		file.setHistorySingletonPropsTreeExpanded(historySingletonPropsTreeExpanded.get());
		file.setHistoryContextPropsTreeExpanded(historyContextPropsTreeExpanded.get());
		file.setFramesColumnOrder(framesColumns.stream().map(c -> c.getColumn()).toList());
		file.setFramesColumnVisible(framesColumns.stream().filter(c -> c.getVisible().get()).map(c -> c.getColumn()).collect(Collectors.toSet()));
		file.setObjectsColumnOrder(objectsColumns.stream().map(c -> c.getColumn()).toList());
		file.setObjectsColumnVisible(objectsColumns.stream().filter(c -> c.getVisible().get()).map(c -> c.getColumn()).collect(Collectors.toSet()));
		file.setObjPropsColumnOrder(objPropsColumns.stream().map(c -> c.getColumn()).toList());
		file.setObjPropsColumnVisible(objPropsColumns.stream().filter(c -> c.getVisible().get()).map(c -> c.getColumn()).collect(Collectors.toSet()));
		
		Files.createDirectories(StoragePaths.GENERAL_PREFERENCES_FILE.getParent());
		MAPPER.writeValue(StoragePaths.GENERAL_PREFERENCES_FILE.toFile(), file);
	}
	
	public GeneralPreferences duplicate() {
		var clone = new GeneralPreferences();
		copyTo(clone);
		return clone;
	}
	
	private void copyTo(GeneralPreferences other) {
		other.fontSize.set(fontSize.get());
		other.historyFileDefaultTab.set(historyFileDefaultTab.get());
		other.saveFileDefaultTab.set(saveFileDefaultTab.get());
		other.bsoTreeExpanded.set(bsoTreeExpanded.get());
		other.historyObjPropsTreeExpanded.set(historyObjPropsTreeExpanded.get());
		other.historySingletonPropsTreeExpanded.set(historySingletonPropsTreeExpanded.get());
		other.historyContextPropsTreeExpanded.set(historyContextPropsTreeExpanded.get());
		other.framesColumns.setAll(framesColumns.stream().map(c -> c.duplicate()).toList());
		other.objectsColumns.setAll(objectsColumns.stream().map(c -> c.duplicate()).toList());
		other.objPropsColumns.setAll(objPropsColumns.stream().map(c -> c.duplicate()).toList());
	}
	
	public SimpleIntegerProperty getFontSize() {
		return fontSize;
	}

	public SimpleObjectProperty<HistoryFileTab> getHistoryFileDefaultTab() {
		return historyFileDefaultTab;
	}
	
	public SimpleObjectProperty<HistoryFileTab> getSaveFileDefaultTab() {
		return saveFileDefaultTab;
	}

	public SimpleBooleanProperty getBsoTreeExpanded() {
		return bsoTreeExpanded;
	}

	public SimpleBooleanProperty getHistoryObjPropsTreeExpanded() {
		return historyObjPropsTreeExpanded;
	}

	public SimpleBooleanProperty getHistorySingletonPropsTreeExpanded() {
		return historySingletonPropsTreeExpanded;
	}

	public SimpleBooleanProperty getHistoryContextPropsTreeExpanded() {
		return historyContextPropsTreeExpanded;
	}

	public ObservableList<TableColumnPreferences<HistoryFramesColumn>> getFramesColumns() {
		return framesColumns;
	}

	public ObservableList<TableColumnPreferences<HistoryObjectsColumn>> getObjectsColumns() {
		return objectsColumns;
	}

	public ObservableList<TableColumnPreferences<ObjectPropertiesColumn>> getObjPropsColumns() {
		return objPropsColumns;
	}

	private static GeneralPreferences loadFromFile() {
		var prefs = new GeneralPreferences();
		
		if (Files.exists(StoragePaths.GENERAL_PREFERENCES_FILE)) {
			try {
				var file = MAPPER.readValue(StoragePaths.GENERAL_PREFERENCES_FILE.toFile(), GeneralPreferencesFile.class);
				prefs.fontSize.set(file.getFontSize());
				prefs.historyFileDefaultTab.set(file.getHistoryFileDefaultTab());
				prefs.saveFileDefaultTab.set(file.getSaveFileDefaultTab());
				prefs.bsoTreeExpanded.set(file.isBsoTreeExpanded());
				prefs.historyObjPropsTreeExpanded.set(file.isHistoryObjPropsTreeExpanded());
				prefs.historySingletonPropsTreeExpanded.set(file.isHistorySingletonPropsTreeExpanded());
				prefs.historyContextPropsTreeExpanded.set(file.isHistoryContextPropsTreeExpanded());
				loadFromFile(HistoryFramesColumn.values(), prefs.framesColumns, file.getFramesColumnOrder(), file.getFramesColumnVisible());
				loadFromFile(HistoryObjectsColumn.values(), prefs.objectsColumns, file.getObjectsColumnOrder(), file.getObjectsColumnVisible());
				loadFromFile(ObjectPropertiesColumn.values(), prefs.objPropsColumns, file.getObjPropsColumnOrder(), file.getObjPropsColumnVisible());
			} catch (IOException e) {
				L.error("Failed to load general prefs file", e);
			}
		} else {
			prefs.resetToDefaults();
		}
		
		return prefs;
	}
	
	private static <T> void loadFromFile(T[] values, ObservableList<TableColumnPreferences<T>> prefs, List<T> columnOrder, Set<T> columnVisible) {
		prefs.addAll(columnOrder.stream().map(c -> new TableColumnPreferences<>(c, columnVisible.contains(c))).toList());
		
		// in case we add more columns in future, or prefs are corrupted
		for (var value : values) {
			if (!columnOrder.contains(value)) {
				prefs.add(new TableColumnPreferences<>(value, false));
			}
		}
	}
	
	private static <E extends Enum<E>> boolean areColumnPrefsEqual(
			ObservableList<TableColumnPreferences<E>> a, ObservableList<TableColumnPreferences<E>> b) {
		for (int i = 0; i < a.size(); i++) { // lists should always contain all elements of the enum
			var aItem = a.get(i);
			var bItem = b.get(i);
			if (aItem.getColumn() != bItem.getColumn() || aItem.getVisible().get() != bItem.getVisible().get()) {
				return false;
			}
		}
		return true;
	}
	
	public static GeneralPreferences getEffective() {
		return EFFECTIVE;
	}
	
}
