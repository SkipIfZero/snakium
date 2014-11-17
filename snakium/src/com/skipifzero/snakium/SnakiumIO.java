package com.skipifzero.snakium;

import com.skipifzero.snakium.framework.io.USBIO;
import com.skipifzero.snakium.model.SnakiumModel;

public final class SnakiumIO {
	private SnakiumIO() { throw new AssertionError(); } //No instantiation.
	
//	//Custom.cfg tags
//	//Board
//	private static final String BOARD_WIDTH_TAG = "BoardWidth";
//	private static final String BOARD_HEIGHT_TAG = "BoardHeight";
//	private static final String HAS_WRAPPING_TAG = "PassThroughWalls";
//	//Speed
//	private static final String TILES_PER_SECOND_TAG = "SpeedTilesPerSecond";
//	private static final String HAS_SPEED_INCREASE_TAG = "IncreasingSpeed";
//	private static final String SPEED_INCREASE_PER_OBJECT_TAG = "SpeedIncreasePerObject";
//	//Bonus
//	private static final String HAS_BONUS_TAG = "HasBonus";
//	private static final String BONUS_FREQUENCY_TAG = "BonusActivationFrequency";
//	private static final String BONUS_DURATION_TAG = "BonusDuration";
//	//Score
//	private static final String POINTS_PER_OBJECT_TAG = "ScorePerNormalObject";
//	private static final String POINTS_PER_BONUS_OBJECT_TAG = "ScorePerBonusObject";
	
	//Paths
	private static final String SNAKIUM_MODEL_SAVEPATH = "skipifzero/snakium/savestate.sav";
	private static final String HIGHSCORES_PATH = "skipifzero/snakium/highscores.sav";
//	private static final String CUSTOM_SNAKIUM_CFG_PATH = "skipifzero/snakium/custom.cfg";
	
	/*
	 * Saved SnakiumModel
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static void saveSnakiumModel(SnakiumModel model) {
		USBIO.writeObjectToFile(SNAKIUM_MODEL_SAVEPATH, model);
	}
	
	public static SnakiumModel readSavedSnakiumModel() {
		Object obj = USBIO.readObjectFromFile(SNAKIUM_MODEL_SAVEPATH);
		
		SnakiumModel model = null;
		try {
			model = (SnakiumModel)obj;
		} catch (ClassCastException e) {
			model = null;
		}
		
		return model;
	}
	
	public static boolean hasSavedSnakiumModel() {
		return USBIO.fileExists(SNAKIUM_MODEL_SAVEPATH);
	}
	
	public static void deleteSavedSnakiumModel() {
		USBIO.deleteFile(SNAKIUM_MODEL_SAVEPATH);
	}
	
	/*
	 * Highscores
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static void saveHighScores(HighScores highScores) {
		USBIO.writeObjectToFile(HIGHSCORES_PATH, highScores);
	}
	
	public static HighScores getHighScores() {
		Object obj = USBIO.readObjectFromFile(HIGHSCORES_PATH);
		
		HighScores highScores = null;
		try {
			highScores = (HighScores)obj;
		} catch (ClassCastException e) {
			highScores = new HighScores();
		}
		
		if(highScores == null) {
			highScores = new HighScores();
		}
		
		return highScores;
	}
	
	/*
	 * Custom Snakium Configuration
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
//	public static SnakiumModel.SnakiumConfig getCustomSnakiumModelConfiguration() {
//		List<String> strings = USBIO.readStringsFromFile(CUSTOM_SNAKIUM_CFG_PATH);
//		if(strings == null) {
//			return null;
//		}
//		
//		StringConfig strCfg = new StringConfig(strings);
//		SnakiumConfigBuilder builder = new SnakiumConfigBuilder();
//		
//		//Board
//		if(strCfg.containsInt(BOARD_WIDTH_TAG)) {
//			builder.boardWidth(strCfg.getInt(BOARD_WIDTH_TAG));
//		}
//		if(strCfg.containsInt(BOARD_HEIGHT_TAG)) {
//			builder.boardHeight(strCfg.getInt(BOARD_HEIGHT_TAG));
//		}
//		if(strCfg.containsBoolean(HAS_WRAPPING_TAG)) {
//			builder.hasWrapping(strCfg.getBoolean(HAS_WRAPPING_TAG));
//		}
//		
//		//Speed
//		if(strCfg.containsDouble(TILES_PER_SECOND_TAG)) {
//			builder.tilesPerSecond(strCfg.getDouble(TILES_PER_SECOND_TAG));
//		}
//		if(strCfg.containsBoolean(HAS_SPEED_INCREASE_TAG)) {
//			builder.hasSpeedIncrease(strCfg.getBoolean(HAS_SPEED_INCREASE_TAG));
//		}
//		if(strCfg.containsDouble(SPEED_INCREASE_PER_OBJECT_TAG)) {
//			builder.speedIncreasePerObject(strCfg.getDouble(SPEED_INCREASE_PER_OBJECT_TAG));
//		}
//		
//		//Bonus
//		if(strCfg.containsBoolean(HAS_BONUS_TAG)) {
//			builder.hasBonus(strCfg.getBoolean(HAS_BONUS_TAG));
//		}
//		if(strCfg.containsInt(BONUS_FREQUENCY_TAG)) {
//			builder.bonusFrequency(strCfg.getInt(BONUS_FREQUENCY_TAG));
//		}
//		if(strCfg.containsInt(BONUS_DURATION_TAG)) {
//			builder.bonusDuration(strCfg.getInt(BONUS_DURATION_TAG));
//		}
//		
//		//Score
//		if(strCfg.containsInt(POINTS_PER_OBJECT_TAG)) {
//			builder.pointsPerObject(strCfg.getInt(POINTS_PER_OBJECT_TAG));
//		}
//		if(strCfg.containsInt(POINTS_PER_BONUS_OBJECT_TAG)) {
//			builder.pointsPerBonusObject(strCfg.getInt(POINTS_PER_BONUS_OBJECT_TAG));
//		}
//		
//		return builder.buildConfiguration();
//	}
//	
//	public static boolean hasCustomSnakiumConfig() {
//		return USBIO.fileExists(CUSTOM_SNAKIUM_CFG_PATH);
//	}
//	
//	public static void createDefaultCustomSnakiumConfig() {
//		StringConfig strCfg = new StringConfig();
//		
//		//Board
//		strCfg.setInt(BOARD_WIDTH_TAG, SnakiumConfigBuilder.DEFAULT_BOARD_WIDTH);
//		strCfg.setInt(BOARD_HEIGHT_TAG, SnakiumConfigBuilder.DEFAULT_BOARD_HEIGHT);
//		strCfg.setBoolean(HAS_WRAPPING_TAG, SnakiumConfigBuilder.DEFAULT_HAS_WRAPPING);
//		//Speed
//		strCfg.setDouble(TILES_PER_SECOND_TAG, SnakiumConfigBuilder.DEFAULT_TILES_PER_SECOND);
//		strCfg.setBoolean(HAS_SPEED_INCREASE_TAG, SnakiumConfigBuilder.DEFAULT_HAS_SPEED_INCREASE);
//		strCfg.setDouble(SPEED_INCREASE_PER_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_SPEED_INCREASE_PER_OBJECT);
//		//Bonus
//		strCfg.setBoolean(HAS_BONUS_TAG, SnakiumConfigBuilder.DEFAULT_HAS_BONUS);
//		strCfg.setInt(BONUS_FREQUENCY_TAG, SnakiumConfigBuilder.DEFAULT_BONUS_FREQUENCY);
//		strCfg.setInt(BONUS_DURATION_TAG, SnakiumConfigBuilder.DEFAULT_BONUS_DURATION);
//		//Score
//		strCfg.setInt(POINTS_PER_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_POINTS_PER_OBJECT);
//		strCfg.setInt(POINTS_PER_BONUS_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_POINTS_PER_BONUS_OBJECT);
//		
//		USBIO.deleteFile(CUSTOM_SNAKIUM_CFG_PATH);
//		USBIO.writeStringsToFile(CUSTOM_SNAKIUM_CFG_PATH, strCfg.toStringRowsAppendRemoveInvalid());
//	}
//	
//	public static void ensureCustomSnakiumConfigHasTags() {
//		List<String> strings = USBIO.readStringsFromFile(CUSTOM_SNAKIUM_CFG_PATH);
//		if(strings == null) {
//			return;
//		}
//		
//		StringConfig strCfg = new StringConfig(strings);
//		
//		//Board
//		if(!strCfg.containsInt(BOARD_WIDTH_TAG)) {
//			strCfg.setInt(BOARD_WIDTH_TAG, SnakiumConfigBuilder.DEFAULT_BOARD_WIDTH);
//		}
//		if(!strCfg.containsInt(BOARD_HEIGHT_TAG)) {
//			strCfg.setInt(BOARD_HEIGHT_TAG, SnakiumConfigBuilder.DEFAULT_BOARD_HEIGHT);
//		}
//		if(!strCfg.containsBoolean(HAS_WRAPPING_TAG)) {
//			strCfg.setBoolean(HAS_WRAPPING_TAG, SnakiumConfigBuilder.DEFAULT_HAS_WRAPPING);
//		}
//		//Speed
//		if(!strCfg.containsDouble(TILES_PER_SECOND_TAG)) {
//			strCfg.setDouble(TILES_PER_SECOND_TAG, SnakiumConfigBuilder.DEFAULT_TILES_PER_SECOND);
//		}
//		if(!strCfg.containsBoolean(HAS_SPEED_INCREASE_TAG)) {
//			strCfg.setBoolean(HAS_SPEED_INCREASE_TAG, SnakiumConfigBuilder.DEFAULT_HAS_SPEED_INCREASE);
//		}
//		if(!strCfg.containsDouble(SPEED_INCREASE_PER_OBJECT_TAG)) {
//			strCfg.setDouble(SPEED_INCREASE_PER_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_SPEED_INCREASE_PER_OBJECT);
//		}
//		//Bonus
//		strCfg.setBoolean(HAS_BONUS_TAG, SnakiumConfigBuilder.DEFAULT_HAS_BONUS);
//		strCfg.setInt(BONUS_FREQUENCY_TAG, SnakiumConfigBuilder.DEFAULT_BONUS_FREQUENCY);
//		strCfg.setInt(BONUS_DURATION_TAG, SnakiumConfigBuilder.DEFAULT_BONUS_DURATION);
//		//Score
//		strCfg.setInt(POINTS_PER_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_POINTS_PER_OBJECT);
//		strCfg.setInt(POINTS_PER_BONUS_OBJECT_TAG, SnakiumConfigBuilder.DEFAULT_POINTS_PER_BONUS_OBJECT);
//	}
}
