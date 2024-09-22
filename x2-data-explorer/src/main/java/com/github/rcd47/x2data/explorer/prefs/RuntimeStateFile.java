package com.github.rcd47.x2data.explorer.prefs;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class RuntimeStateFile {
	
	@JsonInclude(Include.NON_DEFAULT)
	private Path lastOpenDir;
	@JsonInclude(Include.NON_DEFAULT)
	private boolean windowMaximized;
	@JsonInclude(Include.NON_DEFAULT)
	private int windowScreen;
	@JsonInclude(Include.NON_DEFAULT)
	private double windowX;
	@JsonInclude(Include.NON_DEFAULT)
	private double windowY;
	@JsonInclude(Include.NON_DEFAULT)
	private double windowWidth;
	@JsonInclude(Include.NON_DEFAULT)
	private double windowHeight;

	public Path getLastOpenDir() {
		return lastOpenDir;
	}

	public void setLastOpenDir(Path lastOpenDir) {
		this.lastOpenDir = lastOpenDir;
	}

	public boolean isWindowMaximized() {
		return windowMaximized;
	}

	public void setWindowMaximized(boolean windowMaximized) {
		this.windowMaximized = windowMaximized;
	}

	public int getWindowScreen() {
		return windowScreen;
	}

	public void setWindowScreen(int windowScreen) {
		this.windowScreen = windowScreen;
	}

	public double getWindowX() {
		return windowX;
	}

	public void setWindowX(double windowX) {
		this.windowX = windowX;
	}

	public double getWindowY() {
		return windowY;
	}

	public void setWindowY(double windowY) {
		this.windowY = windowY;
	}

	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
	}

	public double getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(double windowHeight) {
		this.windowHeight = windowHeight;
	}
	
}
