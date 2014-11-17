package com.skipifzero.snakium;

import java.util.List;

import com.skipifzero.snakium.framework.io.USBIO;

public final class Settings {
	
	/*
	 * Enums
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static enum ScreenOrientation {
		LAYOUT(1, "layout"),
		PORTRAIT(2, "portrait"), 
		LANDSCAPE(3, "landscape"),
		PORTRAIT_REVERSE(4, "reverse portrait"),
		LANDSCAPE_REVERSE(5, "reverse lanscape");
		
		private final int id;
		private final String name;
		
		private ScreenOrientation(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getID() {
			return id;
		}
		
		public static ScreenOrientation get(int i) {
			for(ScreenOrientation orientation : ScreenOrientation.values()) {
				if(orientation.getID() == i) {
					return orientation;
				}
			}
			throw new IllegalArgumentException("Invalid id");
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/*
	 * Constants
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
		
	private static final boolean DEFAULT_MUSIC = true;
	private static final boolean DEFAULT_SOUND = true;
	private static final boolean DEFAULT_SHOW_BOARD = true;
	private static final boolean DEFAULT_FLOATING_TEXTS = true;
	private static final boolean DEFAULT_SHOW_FPS = false;
	private static final boolean DEFAULT_FULLSCREEN = true;
	//private static final boolean DEFAULT_SCREEN_ORIENTATION_LOCKED = false;
	private static final ScreenOrientation DEFAULT_SCREEN_ORIENTATION = ScreenOrientation.LAYOUT;
	
	private static final String SNAKIUM_INI_PATH = "skipifzero/snakium/snakium.ini";
	
	private static final String TAG_MUSIC = "Music";
	private static final String TAG_SOUND = "Sound";
	private static final String TAG_SHOW_BOARD = "ShowBoard";
	private static final String TAG_FLOATING_TEXTS = "FloatingTexts";
	private static final String TAG_SHOW_FPS = "ShowFPS";
	private static final String TAG_FULLSCREEN = "Fullscreen";
	//private static final String TAG_SCREEN_ORIENTATION_LOCKED = "LockedScreenOrientation";
	private static final String TAG_SCREEN_ORIENTATION = "ScreenOrientation";
	
	/*
	 * Members and constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private static boolean music = DEFAULT_MUSIC;
	private static boolean sound = DEFAULT_SOUND;
	private static boolean showBoard = DEFAULT_SHOW_BOARD;
	private static boolean floatingTexts = DEFAULT_FLOATING_TEXTS;
	private static boolean showFPS = DEFAULT_SHOW_FPS;
	private static boolean fullscreen = DEFAULT_FULLSCREEN;
	//private static boolean screenOrientationLocked = DEFAULT_SCREEN_ORIENTATION_LOCKED;
	private static ScreenOrientation screenOrientation = DEFAULT_SCREEN_ORIENTATION;
	
	//No instantiation.
	private Settings() {
		throw new AssertionError();
	}
	
	public static void setDefaults() {
		Settings.music = DEFAULT_MUSIC;
		Settings.sound = DEFAULT_SOUND;
		Settings.showBoard = DEFAULT_SHOW_BOARD;
		Settings.floatingTexts = DEFAULT_FLOATING_TEXTS;
		Settings.showFPS = DEFAULT_SHOW_FPS;
		Settings.fullscreen = DEFAULT_FULLSCREEN;
		//Settings.screenOrientationLocked = DEFAULT_SCREEN_ORIENTATION_LOCKED;
		Settings.screenOrientation = DEFAULT_SCREEN_ORIENTATION;
	}
	
	public static boolean isDefaults() {
		return 	music == DEFAULT_MUSIC &&
				sound == DEFAULT_SOUND &&
				showBoard == DEFAULT_SHOW_BOARD &&
				floatingTexts == DEFAULT_FLOATING_TEXTS &&
				showFPS == DEFAULT_SHOW_FPS &&
				fullscreen == DEFAULT_FULLSCREEN &&
				/*screenOrientationLocked == DEFAULT_SCREEN_ORIENTATION_LOCKED &&*/
				screenOrientation == DEFAULT_SCREEN_ORIENTATION;
	}
	
	/*
	 * Saving/Loading
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static void load() {
		if(!USBIO.fileExists(SNAKIUM_INI_PATH)) {
			createDefaultIni();
			return;
		}
		
		List<String> strings = USBIO.readStringsFromFile(SNAKIUM_INI_PATH);
		StringConfig strCfg = new StringConfig(strings);
		
		if(strCfg.containsBoolean(TAG_MUSIC)) {
			music = strCfg.getBoolean(TAG_MUSIC);
		}
		if(strCfg.containsBoolean(TAG_SOUND)) {
			sound = strCfg.getBoolean(TAG_SOUND);
		}
		if(strCfg.containsBoolean(TAG_SHOW_BOARD)) {
			showBoard = strCfg.getBoolean(TAG_SHOW_BOARD);
		}
		if(strCfg.containsBoolean(TAG_FLOATING_TEXTS)) {
			floatingTexts = strCfg.getBoolean(TAG_FLOATING_TEXTS);
		}
		if(strCfg.containsBoolean(TAG_SHOW_FPS)) {
			showFPS = strCfg.getBoolean(TAG_SHOW_FPS);
		}
		if(strCfg.containsBoolean(TAG_FULLSCREEN)) {
			fullscreen = strCfg.getBoolean(TAG_FULLSCREEN);
		}
//		if(strCfg.containsBoolean(TAG_SCREEN_ORIENTATION_LOCKED)) {
//			screenOrientationLocked = strCfg.getBoolean(TAG_SCREEN_ORIENTATION_LOCKED);
//		}
		if(strCfg.containsInt(TAG_SCREEN_ORIENTATION)) {
			try {
				screenOrientation = ScreenOrientation.get(strCfg.getInt(TAG_SCREEN_ORIENTATION));
			} catch (IllegalArgumentException e) {
				//Do nothing.
			}
		}
	}
	
	public static void save() {
		StringConfig strCfg = new StringConfig();
		
		strCfg.setBoolean(TAG_MUSIC, music);
		strCfg.setBoolean(TAG_SOUND, sound);
		strCfg.setBoolean(TAG_SHOW_BOARD, showBoard);
		strCfg.setBoolean(TAG_FLOATING_TEXTS, floatingTexts);
		strCfg.setBoolean(TAG_SHOW_FPS, showFPS);
		strCfg.setBoolean(TAG_FULLSCREEN, fullscreen);
		//strCfg.setBoolean(TAG_SCREEN_ORIENTATION_LOCKED, screenOrientationLocked);
		strCfg.setInt(TAG_SCREEN_ORIENTATION, screenOrientation.getID());
		
		USBIO.deleteFile(SNAKIUM_INI_PATH);
		USBIO.writeStringsToFile(SNAKIUM_INI_PATH, strCfg.toStringRowsAppendRemoveInvalid());
	}
	
	private static void createDefaultIni() {
		StringConfig strCfg = new StringConfig();
		
		strCfg.setBoolean(TAG_MUSIC, DEFAULT_MUSIC);
		strCfg.setBoolean(TAG_SOUND, DEFAULT_SOUND);
		strCfg.setBoolean(TAG_SHOW_BOARD, DEFAULT_SHOW_BOARD);
		strCfg.setBoolean(TAG_FLOATING_TEXTS, DEFAULT_FLOATING_TEXTS);
		strCfg.setBoolean(TAG_SHOW_FPS, DEFAULT_SHOW_FPS);
		strCfg.setBoolean(TAG_FULLSCREEN, DEFAULT_FULLSCREEN);
		//strCfg.setBoolean(TAG_SCREEN_ORIENTATION_LOCKED, DEFAULT_SCREEN_ORIENTATION_LOCKED);
		strCfg.setInt(TAG_SCREEN_ORIENTATION, DEFAULT_SCREEN_ORIENTATION.getID());
		
		USBIO.deleteFile(SNAKIUM_INI_PATH);
		USBIO.writeStringsToFile(SNAKIUM_INI_PATH, strCfg.toStringRowsAppendRemoveInvalid());
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static boolean music() {
		return music;
	}
	
	public static boolean sound() {
		return sound;
	}
	
	public static boolean showBoard() {
		return showBoard;
	}
	
	public static boolean floatingTexts() {
		return floatingTexts;
	}
	
	public static boolean showFPS() {
		return showFPS;
	}
	
	public static boolean fullscreen() {
		return fullscreen;
	}
	
//	public static boolean screenOrientationLocked() {
//		return screenOrientationLocked;
//	}
	
	public static ScreenOrientation screenOrientation() {
		return screenOrientation;
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static void music(boolean music) {
		Settings.music = music;
	}
	
	public static void sound(boolean sound) {
		Settings.sound = sound;
	}
	
	public static void showBoard(boolean showBoard) {
		Settings.showBoard = showBoard;
	}
	
	public static void floatingTexts(boolean floatingTexts) {
		Settings.floatingTexts = floatingTexts;
	}
	
	public static void showFPS(boolean showFPS) {
		Settings.showFPS = showFPS;
	}
	
	public static void fullscreen(boolean fullscreen) {
		Settings.fullscreen = fullscreen;
	}
	
//	public static void screenOrientationLocked(boolean screenOrientationLocked) {
//		Settings.screenOrientationLocked = screenOrientationLocked;
//	}
	
	public static void screenOrientation(ScreenOrientation screenOrientation) {
		Settings.screenOrientation = screenOrientation;
	}
}
