package com.github.rcd47.x2data.explorer.jfx;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rcd47.x2data.explorer.file.GenericObjectVisitor;
import com.github.rcd47.x2data.explorer.file.HistoryFileReader;
import com.github.rcd47.x2data.explorer.file.NonVersionedField;
import com.github.rcd47.x2data.explorer.jfx.ui.NonVersionedFieldUI;
import com.github.rcd47.x2data.explorer.jfx.ui.history.HistoryFramesUI;
import com.github.rcd47.x2data.explorer.jfx.ui.history.HistoryGeneralUI;
import com.github.rcd47.x2data.explorer.jfx.ui.history.HistoryProblemsUI;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferences;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.GeneralPreferencesUI;
import com.github.rcd47.x2data.explorer.jfx.ui.prefs.ScriptPreferenceUI;
import com.github.rcd47.x2data.explorer.prefs.HistoryFileTab;
import com.github.rcd47.x2data.explorer.prefs.RuntimeStateFile;
import com.github.rcd47.x2data.explorer.prefs.StoragePaths;
import com.github.rcd47.x2data.explorer.prefs.script.ScriptPreferences;
import com.github.rcd47.x2data.lib.savegame.X2SaveGameReader;
import com.github.rcd47.x2data.lib.unreal.UnrealObjectParser;
import com.github.rcd47.x2data.lib.unreal.UnrealUtils;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealBasicSaveObject;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;
import com.github.rcd47.x2data.lib.unreal.typings.UnrealTypingsBuilder;
import com.google.common.base.Throwables;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static final Logger L = LogManager.getLogger(Main.class);
	
	private Stage stage;
	private TabPane tabPane;
	private Path lastOpenDir;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		
		String version;
		try (var in = getClass().getResourceAsStream("/version.txt")) {
			version = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			L.info("X2 Data Explorer version {}", version);
		} catch (IOException e) {
			// should never happen
			throw new UncheckedIOException(e);
		}
		
		var openFile = new Button("Open File");
		openFile.setOnAction(this::openFile);
		
		var generalPrefs = new MenuItem("General");
		generalPrefs.setOnAction(_ -> new GeneralPreferencesUI());
		
		var stateObjScriptPref = new MenuItem("State Object Summary Script");
		stateObjScriptPref.setOnAction(_ -> new ScriptPreferenceUI(stateObjScriptPref.getText(), ScriptPreferences.STATE_OBJECT_SUMMARY));
		
		var contextScriptPref = new MenuItem("Context Summary Script");
		contextScriptPref.setOnAction(_ -> new ScriptPreferenceUI(contextScriptPref.getText(), ScriptPreferences.CONTEXT_SUMMARY));
		
		var prefs = new MenuButton("Preferences");
		prefs.getItems().addAll(generalPrefs, stateObjScriptPref, contextScriptPref);
		
		var about = new Button("About");
		about.setOnAction(_ -> {
			var alert = new Alert(AlertType.INFORMATION, "X2 Data Explorer version " + version + "\nLicensed under GPL 3.0 or later");
			alert.setTitle("About X2 Data Explorer");
			alert.setHeaderText("About X2 Data Explorer");
			alert.show();
		});
		
		var toolbar = new ToolBar(openFile, prefs, about);
		
		tabPane = new TabPane();
		tabPane.setTabDragPolicy(TabDragPolicy.REORDER);
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		var mainBox = new VBox(toolbar, tabPane);
		mainBox.styleProperty().bind(GeneralPreferences.getEffective().getFontSize().map(s -> "-fx-font-size: " + s + "pt"));
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		
		var scene = new Scene(mainBox);
		scene.getStylesheets().add("/styles.css");
		scene.setOnDragOver(event -> {
			if (event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				event.consume();
			}
		});
		scene.setOnDragDropped(event -> {
			for (var file : event.getDragboard().getFiles()) {
				openFile(file);
			}
			event.consume();
		});
		
		var runtimeStateMapper = new ObjectMapper();
		
		boolean useDefaultPosition = true;
		if (Files.exists(StoragePaths.RUNTIME_STATE_FILE)) {
			// must be careful here in case display configuration changed
			try {
				var state = runtimeStateMapper.readValue(StoragePaths.RUNTIME_STATE_FILE.toFile(), RuntimeStateFile.class);
				lastOpenDir = state.getLastOpenDir();
				if (state.isWindowMaximized()) {
					var screens = List.copyOf(Screen.getScreens());
					var screenIndex = state.getWindowScreen();
					if (screenIndex < screens.size()) {
						centerAndMaximizeWindow(screens.get(screenIndex).getBounds());
						useDefaultPosition = false;
					}
				} else if (!Screen
						.getScreensForRectangle(state.getWindowX(), state.getWindowY(), state.getWindowWidth(), state.getWindowHeight())
						.isEmpty()) {
					stage.setX(state.getWindowX());
					stage.setY(state.getWindowY());
					stage.setWidth(state.getWindowWidth());
					stage.setHeight(state.getWindowHeight());
					useDefaultPosition = false;
				}
			} catch (IOException e) {
				L.error("Failed to load runtime state file", e);
			}
		}
		if (useDefaultPosition) {
			centerAndMaximizeWindow(Screen.getPrimary().getBounds());
		}
		
		stage.setOnCloseRequest(_ -> {
			var state = new RuntimeStateFile();
			state.setLastOpenDir(lastOpenDir);
			if (stage.isMaximized()) {
				// when maximized, the reported values can extend outside the bounds of the screen
				// so look for the screen that contains the center of the window
				var centerX = (stage.getX() + stage.getWidth()) / 2;
				var centerY = (stage.getY() + stage.getHeight()) / 2;
				var screens = List.copyOf(Screen.getScreens());
				for (int i = 0; i < screens.size(); i++) {
					if (screens.get(i).getBounds().contains(centerX, centerY)) {
						state.setWindowScreen(i);
						break;
					}
				}
				state.setWindowMaximized(true);
			} else {
				state.setWindowX(stage.getX());
				state.setWindowY(stage.getY());
				state.setWindowWidth(stage.getWidth());
				state.setWindowHeight(stage.getHeight());
			}
			
			try {
				Files.createDirectories(StoragePaths.BASE_PATH);
				runtimeStateMapper.writeValue(StoragePaths.RUNTIME_STATE_FILE.toFile(), state);
			} catch (IOException e) {
				L.error("Failed to save runtime state file", e);
			}
		});
		stage.setTitle("X2 Data Explorer " + version);
		stage.setScene(scene);
		stage.show();
	}
	
	private void centerAndMaximizeWindow(Rectangle2D screenBounds) {
		stage.setWidth(screenBounds.getWidth() * 0.75);
		stage.setHeight(screenBounds.getHeight() * 0.75);
		stage.centerOnScreen();
		stage.setMaximized(true);
	}
	
	private void openFile(ActionEvent event) {
		var fileChooser = new FileChooser();
		if (lastOpenDir != null) {
			fileChooser.setInitialDirectory(lastOpenDir.toFile());
		}
		var selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			openFile(selectedFile);
		}
	}
	
	private void openFile(File selectedFile) {
		var selectedPath = selectedFile.toPath();
		lastOpenDir = selectedPath.getParent();
		var tab = new Tab(selectedPath.toString());
		FileChannel in = null;
		try {
			in = FileChannel.open(selectedPath, Set.of(StandardOpenOption.READ));
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
				loadBasicSaveObjectFile(tab, in, selectedPath);
			} else if (typeDetectBuffer.getInt(0) == UnrealUtils.UNREAL_MAGIC_NUMBER) {
				loadHistoryFile(tab, in, false);
			} else {
				loadHistoryFile(tab, in, true);
			}
		} catch (IOException e) {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e2) {
				e.addSuppressed(e2);
			}
			tab.setContent(readingFileFailed(e));
		}
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}
	
	private void loadBasicSaveObjectFile(Tab tab, FileChannel in, Path file) {
		// if we replace immediate children of the SplitPane, we lose the divider position
		var splitLeft = new VBox();
		var splitRight = new VBox();
		splitRight.setAlignment(Pos.CENTER);
		var splitPane = new SplitPane(splitLeft, splitRight);
		splitPane.setDividerPositions(0.8);
		tab.setContent(splitPane);
		
		var task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					// not bothering to track progress because BSO files are tiny and load very fast
					updateMessage("Loading file");
					
					var typings = new UnrealTypingsBuilder().buildBasicSaveObjects();
					
					var typePicker = new ListView<>(FXCollections.observableList(typings
							.values()
							.stream()
							// BSO can contain structs, which will be present in the typings but should not be selectable
							.filter(t -> t.mappedType.isAnnotationPresent(UnrealBasicSaveObject.class))
							.sorted((a, b) -> a.unrealTypeName.compareTo(b.unrealTypeName))
							.map(t -> t.unrealTypeName)
							.toList()));
					typePicker.setCellFactory(_ -> {
						var cell = new ListCell<UnrealName>();
						cell.textProperty().bind(cell.itemProperty().map(UnrealName::getOriginal));
						return cell;
					});
					VBox.setVgrow(typePicker, Priority.ALWAYS);
					
					var typePickerHeader = new Label("Interpret As");
					typePickerHeader.setStyle("-fx-font-weight: bold");
					Platform.runLater(() -> splitRight.getChildren().addAll(typePickerHeader, typePicker));
					
					var buffer = ByteBuffer.allocate((int) in.size()).order(ByteOrder.LITTLE_ENDIAN);
					in.read(buffer);
					buffer.flip();
					
					var parser = new UnrealObjectParser(false, typings);
					
					// note that Nodes can be created on any thread, as long as they are not added to the Scene
					var objPropsUI = new NonVersionedFieldUI(GeneralPreferences.getEffective().getBsoTreeExpanded(), "No properties parsed");
					
					typePicker.getSelectionModel().selectedItemProperty().addListener(
							(_, _, newVal) -> parseBasicSaveObjectFile(parser, newVal, splitLeft, objPropsUI, buffer));
					
					// BSO format doesn't indicate the type of the object
					// so our convention is that the type will be at the beginning of the filename, delimited by a space
					typePicker.getSelectionModel().select(new UnrealName(file.getFileName().toString().split(" ")[0]));
					
					return null;
				} finally {
					in.close();
				}
			}

			@Override
			protected void failed() {
				splitPane.getItems().set(0, readingFileFailed(getException()));
			}
		};
		
		new Thread(task, "BsoFileLoader").start();
	}
	
	private void parseBasicSaveObjectFile(
			UnrealObjectParser parser, UnrealName type, VBox splitLeft, NonVersionedFieldUI objPropsUI, ByteBuffer buffer) {
		var task = new Task<TreeItem<Map.Entry<UnrealName, NonVersionedField>>>() {
			@Override
			protected TreeItem<Map.Entry<UnrealName, NonVersionedField>> call() throws Exception {
				buffer.rewind();
				var visitor = new GenericObjectVisitor(null);
				parser.parse(type, buffer, visitor);
				return NonVersionedField.convertToTreeItems(new NonVersionedField(visitor.getRootObject()).getChildren());
			}

			@Override
			protected void succeeded() {
				var objPropsNode = objPropsUI.getNode();
				VBox.setVgrow(objPropsNode, Priority.ALWAYS);
				splitLeft.getChildren().setAll(objPropsNode);
				objPropsUI.getRootProperty().set(getValue());
			}

			@Override
			protected void failed() {
				splitLeft.getChildren().setAll(readingFileFailed(getException()));
			}
		};
		
		new Thread(task, "BsoFileParser").start();
	}
	
	private void loadHistoryFile(Tab tab, FileChannel in, boolean isSaveFile) {
		var task = new Task<Node>() {
			@Override
			protected Node call() throws Exception {
				try {
					var header = isSaveFile ? new X2SaveGameReader().readHeader(in) : null;
					var history = new HistoryFileReader().read(in, p -> updateProgress(p, 1), this::updateMessage);
					
					var generalTab = new Tab(HistoryFileTab.GENERAL.getTabTitle(), new HistoryGeneralUI(header, history).getNode());
					generalTab.setClosable(false);
					
					var framesTab = new Tab(HistoryFileTab.FRAMES.getTabTitle(), new HistoryFramesUI(history).getNode());
					framesTab.setClosable(false);
					
					var problemsCount = history.getProblems().size();
					var problemsTab = new Tab(
							HistoryFileTab.PROBLEMS.getTabTitle() + " (" + problemsCount + ")", new HistoryProblemsUI(history).getNode());
					problemsTab.setClosable(false);
					if (problemsCount > 0) {
						problemsTab.getStyleClass().add("historyProblemsFound");
					}
					
					var defaultTabConfig = header == null ?
							GeneralPreferences.getEffective().getHistoryFileDefaultTab() : GeneralPreferences.getEffective().getSaveFileDefaultTab();
					var defaultTab = switch (defaultTabConfig.get()) {
						case FRAMES -> framesTab;
						case GENERAL -> generalTab;
						case PROBLEMS -> problemsTab;
					};
					
					var tabPane = new TabPane(generalTab, framesTab, problemsTab);
					tabPane.getSelectionModel().select(defaultTab);
					
					return tabPane;
				} finally {
					in.close();
				}
			}

			@Override
			protected void succeeded() {
				tab.setContent(getValue());
			}

			@Override
			protected void failed() {
				tab.setContent(readingFileFailed(getException()));
			}
		};
		
		tab.setContent(readingFileStarted(task));
		
		new Thread(task, "HistoryFileLoader").start();
	}
	
	private static Node readingFileStarted(Task<?> task) {
		var progressBar = new ProgressBar();
		progressBar.setPrefWidth(400);
		progressBar.progressProperty().bind(task.progressProperty());
		
		var progressText = new Text();
		progressText.textProperty().bind(task.messageProperty());
		
		var vbox = new VBox(5, progressBar, progressText);
		vbox.setAlignment(Pos.BASELINE_CENTER);
		vbox.setPadding(new Insets(10));
		
		return vbox;
	}
	
	private static Node readingFileFailed(Throwable t) {
		var text = new Text(Throwables.getStackTraceAsString(t));
		text.setFill(Color.RED);
		
		var copyMenuItem = new MenuItem("Copy to clipboard");
		copyMenuItem.setOnAction(_ -> {
			var content = new ClipboardContent();
			content.putString(text.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
		
		var scrollPane = new ScrollPane(text);
		scrollPane.setPadding(new Insets(10));
		scrollPane.setContextMenu(new ContextMenu(copyMenuItem));
		
		return scrollPane;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}
