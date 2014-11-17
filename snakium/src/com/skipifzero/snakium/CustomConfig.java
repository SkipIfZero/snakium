package com.skipifzero.snakium;

import com.skipifzero.snakium.framework.io.USBIO;
import com.skipifzero.snakium.model.SnakiumConfigBuilder;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;

public final class CustomConfig {
	private CustomConfig() { throw new AssertionError(); } //No instantiation.
	
	private static final String CUSTOM_SNAKIUM_CFG_PATH = "skipifzero/snakium/custom.cfg";
	
	//Custom.cfg tags
	//Board
	private static final String BOARD_WIDTH_TAG = "BoardWidth";
	private static final String BOARD_HEIGHT_TAG = "BoardHeight";
	private static final String HAS_WRAPPING_TAG = "PassThroughWalls";
	//Speed
	private static final String TILES_PER_SECOND_TAG = "SpeedTilesPerSecond";
	private static final String HAS_SPEED_INCREASE_TAG = "IncreasingSpeed";
	private static final String SPEED_INCREASE_PER_OBJECT_TAG = "SpeedIncreasePerObject";
	//Bonus
	private static final String HAS_BONUS_TAG = "HasBonus";
	private static final String BONUS_FREQUENCY_TAG = "BonusActivationFrequency";
	private static final String BONUS_DURATION_TAG = "BonusDuration";
	//Score
	private static final String POINTS_PER_OBJECT_TAG = "ScorePerNormalObject";
	private static final String POINTS_PER_BONUS_OBJECT_TAG = "ScorePerBonusObject";
	
	//Limits
	//Board
	private static final int MIN_BOARD_WIDTH = 5;
	private static final int MAX_BOARD_WIDTH = 30;
	private static final int MIN_BOARD_HEIGHT = 5;
	private static final int MAX_BOARD_HEIGHT = 30;
	//Speed
	private static final double MIN_TILES_PER_SECOND = 0.2;
	private static final double MAX_TILES_PER_SECOND = 120;
	private static final double MIN_SPEED_INCREASE = 0.0;
	private static final double MAX_SPEED_INCREASE = 10;
	//Bonus
	private static final int MIN_BONUS_FREQUENCY = 0;
	private static final int MAX_BONUS_FREQUENCY = MAX_BOARD_HEIGHT*MAX_BOARD_WIDTH;
	private static final int MIN_BONUS_DURATION = 0;
	private static final int MAX_BONUS_DURATION = 999;
	//Score
	private static final int MIN_POINTS_PER_OBJECT = 0;
	private static final int MAX_POINTS_PER_OBJECT = 100;
	private static final int MIN_POINTS_PER_BONUS_OBJECT = 0;
	private static final int MAX_POINTS_PER_BONUS_OBJECT = 100;
	
	private static StringConfig strCfg = null;
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static boolean load() {
		if(!USBIO.fileExists(CUSTOM_SNAKIUM_CFG_PATH)) {
			set(SnakiumConfigBuilder.SNAKIUM_BASE_CONFIG);
			return true;
		}
		CustomConfig.strCfg = new StringConfig(USBIO.readStringsFromFile(CUSTOM_SNAKIUM_CFG_PATH));
		return strCfg != null;
	}
	
	public static void saveToFile() {
		if(strCfg != null) {
			USBIO.deleteFile(CUSTOM_SNAKIUM_CFG_PATH);
			USBIO.writeStringsToFile(CUSTOM_SNAKIUM_CFG_PATH, strCfg.toStringRowsAppendRemoveInvalid());
		}
	}
	
	public static boolean isLoaded() {
		return strCfg != null;
	}
	
	public static boolean fileExists() {
		return USBIO.fileExists(CUSTOM_SNAKIUM_CFG_PATH);
	}
	
	public static void set(SnakiumConfig config) {
		CustomConfig.strCfg = new StringConfig();
		
		//Board
		boardWidth(config.boardWidth);
		boardHeight(config.boardHeight);
		hasWrapping(config.hasWrapping);
		//Speed
		tilesPerSecond(config.tilesPerSecond);
		hasSpeedIncrease(config.hasSpeedIncrease);
		speedIncreasePerObject(config.speedIncreasePerObject);
		//Bonus
		hasBonus(config.hasBonus);
		bonusFrequency(config.bonusFrequency);
		bonusDuration(config.bonusDuration);
		//Score
		pointsPerObject(config.pointsPerObject);
		pointsPerBonusObject(config.pointsPerBonusObject);
	}
	
	public static SnakiumConfig getSnakiumConfig() {
		stringConfigExists();
		
		SnakiumConfigBuilder builder = new SnakiumConfigBuilder();
		
		//Board
		builder.boardWidth(boardWidth());
		builder.boardHeight(boardHeight());
		builder.hasWrapping(hasWrapping());
		//Speed
		builder.tilesPerSecond(tilesPerSecond());
		builder.hasSpeedIncrease(hasSpeedIncrease());
		builder.speedIncreasePerObject(speedIncreasePerObject());
		//Bonus
		builder.hasBonus(hasBonus());
		builder.bonusFrequency(bonusFrequency());
		builder.bonusDuration(bonusDuration());
		//Score
		builder.pointsPerObject(pointsPerObject());
		builder.pointsPerBonusObject(pointsPerBonusObject());
		
		return builder.buildConfiguration();
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static int boardWidth() {
		stringConfigExists();
		if(!strCfg.containsInt(BOARD_WIDTH_TAG)) {
			boardWidth(SnakiumConfigBuilder.DEFAULT_BOARD_WIDTH);
		}
		return strCfg.getInt(BOARD_WIDTH_TAG);
	}
	
	public static int boardHeight() {
		stringConfigExists();
		if(!strCfg.containsInt(BOARD_HEIGHT_TAG)) {
			boardHeight(SnakiumConfigBuilder.DEFAULT_BOARD_HEIGHT);
		}
		return strCfg.getInt(BOARD_HEIGHT_TAG);
	}
	
	public static boolean hasWrapping() {
		stringConfigExists();
		if(!strCfg.containsBoolean(HAS_WRAPPING_TAG)) {
			hasWrapping(SnakiumConfigBuilder.DEFAULT_HAS_WRAPPING);
		}
		return strCfg.getBoolean(HAS_WRAPPING_TAG);
	}
	
	public static double tilesPerSecond() {
		stringConfigExists();
		if(!strCfg.containsDouble(TILES_PER_SECOND_TAG)) {
			tilesPerSecond(SnakiumConfigBuilder.DEFAULT_TILES_PER_SECOND);
		}
		return strCfg.getDouble(TILES_PER_SECOND_TAG);
	}
	
	public static boolean hasSpeedIncrease() {
		stringConfigExists();
		if(!strCfg.containsBoolean(HAS_SPEED_INCREASE_TAG)) {
			hasSpeedIncrease(SnakiumConfigBuilder.DEFAULT_HAS_SPEED_INCREASE);
		}
		return strCfg.getBoolean(HAS_SPEED_INCREASE_TAG);
	}
	
	public static double speedIncreasePerObject() {
		stringConfigExists();
		if(!strCfg.containsDouble(SPEED_INCREASE_PER_OBJECT_TAG)) {
			speedIncreasePerObject(SnakiumConfigBuilder.DEFAULT_SPEED_INCREASE_PER_OBJECT);
		}
		return strCfg.getDouble(SPEED_INCREASE_PER_OBJECT_TAG);
	}
	
	public static boolean hasBonus() {
		stringConfigExists();
		if(!strCfg.containsBoolean(HAS_BONUS_TAG)) {
			hasBonus(SnakiumConfigBuilder.DEFAULT_HAS_BONUS);
		}
		return strCfg.getBoolean(HAS_BONUS_TAG);
	}
	
	public static int bonusFrequency() {
		stringConfigExists();
		if(!strCfg.containsInt(BONUS_FREQUENCY_TAG)) {
			bonusFrequency(SnakiumConfigBuilder.DEFAULT_BONUS_FREQUENCY);
		}
		return strCfg.getInt(BONUS_FREQUENCY_TAG);
	}
	
	public static int bonusDuration() {
		stringConfigExists();
		if(!strCfg.containsInt(BONUS_DURATION_TAG)) {
			bonusDuration(SnakiumConfigBuilder.DEFAULT_BONUS_DURATION);
		}
		return strCfg.getInt(BONUS_DURATION_TAG);
	}
	
	public static int pointsPerObject() {
		stringConfigExists();
		if(!strCfg.containsInt(POINTS_PER_OBJECT_TAG)) {
			pointsPerObject(SnakiumConfigBuilder.DEFAULT_POINTS_PER_OBJECT);
		}
		return strCfg.getInt(POINTS_PER_OBJECT_TAG);
	}
	
	public static int pointsPerBonusObject() {
		stringConfigExists();
		if(!strCfg.containsInt(POINTS_PER_BONUS_OBJECT_TAG)) {
			pointsPerBonusObject(SnakiumConfigBuilder.DEFAULT_POINTS_PER_BONUS_OBJECT);
		}
		return strCfg.getInt(POINTS_PER_BONUS_OBJECT_TAG);
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static void boardWidth(int boardWidth) {
		stringConfigExists();
		strCfg.setInt(BOARD_WIDTH_TAG, boardWidthCheck(boardWidth));
	}
	
	public static void boardHeight(int boardHeight) {
		stringConfigExists();
		strCfg.setInt(BOARD_HEIGHT_TAG, boardHeightCheck(boardHeight));
	}
	
	public static void hasWrapping(boolean hasWrapping) {
		stringConfigExists();
		strCfg.setBoolean(HAS_WRAPPING_TAG, hasWrapping);
	}
	
	public static void tilesPerSecond(double tilesPerSecond) {
		stringConfigExists();
		strCfg.setDouble(TILES_PER_SECOND_TAG, tilesPerSecondCheck(tilesPerSecond));
	}
	
	public static void hasSpeedIncrease(boolean hasSpeedIncrease) {
		stringConfigExists();
		strCfg.setBoolean(HAS_SPEED_INCREASE_TAG, hasSpeedIncrease);
	}
	
	public static void speedIncreasePerObject(double speedIncreasePerObject) {
		stringConfigExists();
		strCfg.setDouble(SPEED_INCREASE_PER_OBJECT_TAG, speedIncreaseCheck(speedIncreasePerObject));
	}
	
	public static void hasBonus(boolean hasBonus) {
		stringConfigExists();
		strCfg.setBoolean(HAS_BONUS_TAG, hasBonus);
	}
	
	public static void bonusFrequency(int bonusFrequency) {
		stringConfigExists();
		strCfg.setInt(BONUS_FREQUENCY_TAG, bonusFrequencyCheck(bonusFrequency));
	}
	
	public static void bonusDuration(int bonusDuration) {
		stringConfigExists();
		strCfg.setInt(BONUS_DURATION_TAG, bonusDurationCheck(bonusDuration));
	}
	
	public static void pointsPerObject(int pointsPerObject) {
		stringConfigExists();
		strCfg.setInt(POINTS_PER_OBJECT_TAG, pointsPerObjectCheck(pointsPerObject));
	}
	
	public static void pointsPerBonusObject(int pointsPerBonusObject) {
		stringConfigExists();
		strCfg.setInt(POINTS_PER_BONUS_OBJECT_TAG, pointsPerBonusObjectCheck(pointsPerBonusObject));
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private static void stringConfigExists() {
		if(strCfg == null) {
			throw new IllegalStateException("Not loaded, internal string config is null.");
		}
	}
	
	private static int boardWidthCheck(int boardWidth) {
		if(boardWidth < MIN_BOARD_WIDTH) {
			return MIN_BOARD_WIDTH;
		} else if(MAX_BOARD_WIDTH < boardWidth) {
			return MAX_BOARD_WIDTH;
		}
		return boardWidth;
	}
	
	private static int boardHeightCheck(int boardHeight) {
		if(boardHeight < MIN_BOARD_HEIGHT) {
			return MIN_BOARD_HEIGHT;
		} else if(MAX_BOARD_HEIGHT < boardHeight) {
			return MAX_BOARD_HEIGHT;
		}
		return boardHeight;
	}
	
	private static double tilesPerSecondCheck(double tilesPerSecond) {
		if(tilesPerSecond < MIN_TILES_PER_SECOND) {
			return MIN_TILES_PER_SECOND;
		} else if(MAX_TILES_PER_SECOND < tilesPerSecond) {
			return MAX_TILES_PER_SECOND;
		}
		return tilesPerSecond;
	}
	
	private static double speedIncreaseCheck(double speedIncrease) {
		if(speedIncrease < MIN_SPEED_INCREASE) {
			return MIN_SPEED_INCREASE;
		} else if(MAX_SPEED_INCREASE < speedIncrease) {
			return MAX_SPEED_INCREASE;
		}
		return speedIncrease;
	}
	
	private static int bonusFrequencyCheck(int bonusFrequency) {
		if(bonusFrequency < MIN_BONUS_FREQUENCY) {
			return MIN_BONUS_FREQUENCY;
		} else if(MAX_BONUS_FREQUENCY < bonusFrequency) {
			return MAX_BONUS_FREQUENCY;
		}
		return bonusFrequency;
	}
	
	private static int bonusDurationCheck(int bonusDuration) {
		if(bonusDuration < MIN_BONUS_DURATION) {
			return MIN_BONUS_DURATION;
		} else if(MAX_BONUS_DURATION < bonusDuration) {
			return MAX_BONUS_DURATION;
		}
		return bonusDuration;
	}
	
	private static int pointsPerObjectCheck(int pointsPerObject) {
		if(pointsPerObject < MIN_POINTS_PER_OBJECT) {
			return MIN_POINTS_PER_OBJECT;
		} else if(MAX_POINTS_PER_OBJECT < pointsPerObject) {
			return MAX_POINTS_PER_OBJECT;
		}
		return pointsPerObject;
	}
	
	private static int pointsPerBonusObjectCheck(int pointsPerBonusObject) {
		if(pointsPerBonusObject < MIN_POINTS_PER_BONUS_OBJECT) {
			return MIN_POINTS_PER_BONUS_OBJECT;
		} else if(MAX_POINTS_PER_BONUS_OBJECT < pointsPerBonusObject) {
			return MAX_POINTS_PER_BONUS_OBJECT;
		}
		return pointsPerBonusObject;
	}
}
