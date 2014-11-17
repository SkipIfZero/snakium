package com.skipifzero.snakium.model;

import java.io.Serializable;

import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;

/**
 * Class used for building SnakiumConfigs for SnakiumModels.
 * 
 * @author Peter Hillerström
 * @since 2013-08-15
 * @version 2
 */
public final class SnakiumConfigBuilder implements Serializable {
	
	/**
	 * serialVersionUID == class version
	 */
	private static final long serialVersionUID = 2L;
	
	public static final int CLASSIC_CONFIG_ID = 1;
	public static final int SNAKE2_CONFIG_ID = 2;
	public static final int SNAKIUM_CONFIG_ID = 3;
	
	private static final int	DEFAULT_ID = -1;
	public static final boolean DEFAULT_HAS_WRAPPING = true;
	public static final int 	DEFAULT_BOARD_WIDTH = 12;
	public static final int 	DEFAULT_BOARD_HEIGHT = 12;
	public static final double 	DEFAULT_TILES_PER_SECOND = 4;
	public static final boolean DEFAULT_HAS_SPEED_INCREASE = true;
	public static final double	DEFAULT_SPEED_INCREASE_PER_OBJECT = 0.025;
	public static final int		DEFAULT_POINTS_PER_OBJECT = 8;
	public static final boolean DEFAULT_HAS_BONUS = true;
	public static final int	 	DEFAULT_BONUS_FREQUENCY = 6;
	public static final int 	DEFAULT_BONUS_DURATION = 20;
	public static final int		DEFAULT_POINTS_PER_BONUS_OBJECT = 32;
	
	public static final SnakiumModel.SnakiumConfig 	CLASSIC_BASE_CONFIG, SNAKE2_BASE_CONFIG, SNAKIUM_BASE_CONFIG, 
													CLASSIC_WIDE_CONFIG, CLASSIC_HIGH_CONFIG,
													SNAKE2_WIDE_CONFIG, SNAKE2_HIGH_CONFIG,
													SNAKIUM_WIDE_CONFIG, SNAKIUM_HIGH_CONFIG;
	static {
		CLASSIC_BASE_CONFIG = new SnakiumConfigBuilder()
						.id(CLASSIC_CONFIG_ID)
						.hasWrapping(false)
						.hasSpeedIncrease(false)
						.speedIncreasePerObject(0)
						.hasBonus(false)
						.bonusFrequency(0)
						.bonusDuration(0)
						.pointsPerBonusObject(0)
						.buildConfiguration();
		
		SNAKE2_BASE_CONFIG = new SnakiumConfigBuilder()
						.id(SNAKE2_CONFIG_ID)
						.hasWrapping(true)
						.hasSpeedIncrease(false)
						.speedIncreasePerObject(0)
						.hasBonus(true)
						.bonusDuration(16)
						.buildConfiguration();
		
		SNAKIUM_BASE_CONFIG = new SnakiumConfigBuilder()
						.id(SNAKIUM_CONFIG_ID)
						.hasWrapping(true)
						.tilesPerSecond(3.5)
						.hasSpeedIncrease(true)
						.hasBonus(true)
						.buildConfiguration();
		
		
		CLASSIC_WIDE_CONFIG = new SnakiumConfigBuilder()
							.id(CLASSIC_CONFIG_ID)
							.set(CLASSIC_BASE_CONFIG)
							.boardWidth(14)
							.boardHeight(10)
							.buildConfiguration();
		
		CLASSIC_HIGH_CONFIG = new SnakiumConfigBuilder()
							.id(CLASSIC_CONFIG_ID)
							.set(CLASSIC_BASE_CONFIG)
							.boardWidth(10)
							.boardHeight(14)
							.buildConfiguration();
		
		
		SNAKE2_WIDE_CONFIG = new SnakiumConfigBuilder()
							.id(SNAKE2_CONFIG_ID)
							.set(SNAKE2_BASE_CONFIG)
							.boardWidth(16)
							.boardHeight(10)
							.buildConfiguration();

		SNAKE2_HIGH_CONFIG = new SnakiumConfigBuilder()
							.id(SNAKE2_CONFIG_ID)
							.set(SNAKE2_BASE_CONFIG)
							.boardWidth(10)
							.boardHeight(16)
							.buildConfiguration();

		
		SNAKIUM_WIDE_CONFIG = new SnakiumConfigBuilder()
							.id(SNAKIUM_CONFIG_ID)
							.set(SNAKIUM_BASE_CONFIG)
							.boardWidth(18)
							.boardHeight(12)
							.buildConfiguration();
		
		SNAKIUM_HIGH_CONFIG = new SnakiumConfigBuilder()
							.id(SNAKIUM_CONFIG_ID)
							.set(SNAKIUM_BASE_CONFIG)
							.boardWidth(12)
							.boardHeight(18)
							.buildConfiguration();
	}
	
	private int id = DEFAULT_ID;
	private boolean hasWrapping = DEFAULT_HAS_WRAPPING;
	private int boardWidth = DEFAULT_BOARD_WIDTH;
	private int boardHeight = DEFAULT_BOARD_HEIGHT;
	private double tilesPerSecond = DEFAULT_TILES_PER_SECOND;
	private boolean hasSpeedIncrease = DEFAULT_HAS_SPEED_INCREASE;
	private double speedIncreasePerObject = DEFAULT_SPEED_INCREASE_PER_OBJECT;
	private int pointsPerObject = DEFAULT_POINTS_PER_OBJECT;
	private boolean hasBonus = DEFAULT_HAS_BONUS;
	private int bonusFrequency = DEFAULT_BONUS_FREQUENCY;
	private int bonusDuration = DEFAULT_BONUS_DURATION;
	private int pointsPerBonusObject = DEFAULT_POINTS_PER_BONUS_OBJECT;
	
	/*
	 * Custom Builder
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public SnakiumConfigBuilder() {
	}
	
	public SnakiumConfig buildConfiguration() {
		return new SnakiumConfig(	id,
									hasWrapping, 
									boardWidth, 
									boardHeight, 
									tilesPerSecond, 
									hasSpeedIncrease, 
									speedIncreasePerObject, 
									pointsPerObject, 
									hasBonus, 
									bonusFrequency, 
									bonusDuration, 
									pointsPerBonusObject);
	}
	
	private SnakiumConfigBuilder id(int id) {
		this.id = id;
		return this;
	}
	
	public SnakiumConfigBuilder set(SnakiumConfig config) {
		//Doesn't set id. Safety reasons.
		hasWrapping(config.hasWrapping);
		boardWidth(config.boardWidth);
		boardHeight(config.boardHeight);
		tilesPerSecond(config.tilesPerSecond);
		hasSpeedIncrease(config.hasSpeedIncrease);
		speedIncreasePerObject(config.speedIncreasePerObject);
		pointsPerObject(config.pointsPerObject);
		hasBonus(config.hasBonus);
		bonusFrequency(config.bonusFrequency);
		bonusDuration(config.bonusDuration);
		pointsPerBonusObject(config.pointsPerBonusObject);
		return this;
	}
	
	public SnakiumConfigBuilder hasWrapping(boolean hasWrapping) {
		this.hasWrapping = hasWrapping;
		return this;
	}
	
	public SnakiumConfigBuilder boardWidth(int boardWidth) {
		this.boardWidth = boardWidth;
		return this;
	}
	
	public SnakiumConfigBuilder boardHeight(int boardHeight) {
		this.boardHeight = boardHeight;
		return this;
	}
	
	public SnakiumConfigBuilder tilesPerSecond(double tilesPerSecond) {
		this.tilesPerSecond = tilesPerSecond;
		return this;
	}
	
	public SnakiumConfigBuilder hasSpeedIncrease(boolean hasSpeedIncrease) {
		this.hasSpeedIncrease = hasSpeedIncrease;
		return this;
	}
	
	public SnakiumConfigBuilder speedIncreasePerObject(double speedIncreasePerObject) {
		this.speedIncreasePerObject = speedIncreasePerObject;
		return this;
	}
	
	public SnakiumConfigBuilder pointsPerObject(int pointsPerObject) {
		this.pointsPerObject = pointsPerObject;
		return this;
	}
	
	public SnakiumConfigBuilder hasBonus(boolean hasBonus) {
		this.hasBonus = hasBonus;
		return this;
	}
	
	public SnakiumConfigBuilder bonusFrequency(int bonusFrequency) {
		this.bonusFrequency = bonusFrequency;
		return this;
	}
	
	public SnakiumConfigBuilder bonusDuration(int bonusDuration) {
		this.bonusDuration = bonusDuration;
		return this;
	}
	
	public SnakiumConfigBuilder pointsPerBonusObject(int pointsPerBonusObject) {
		this.pointsPerBonusObject = pointsPerBonusObject;
		return this;
	}
}
