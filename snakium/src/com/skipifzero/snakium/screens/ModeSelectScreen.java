package com.skipifzero.snakium.screens;

import java.util.List;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.CustomConfig;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.editor.EditorScreen;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.model.SnakiumConfigBuilder;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;

public class ModeSelectScreen extends SnakiumGLScreen {

	private static enum MenuButtons {
		SNAKIUM("snakium"),
		SNAKE2("snake 2"),
		CLASSIC("classic"),
		CUSTOM("custom"),
		EDITOR("editor");
		
		private final String name;
		private TouchButton button;
		
		private MenuButtons(String name) {
			this.name = name;
		}
		
		public void setButton(TouchButton button) {
			this.button = button;
		}
		
		public TouchButton getButton() {
			return button;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static final String SCREEN_TITLE = "select mode";
	private static final double MENU_BUTTON_HEIGHT = (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - (MenuButtons.values().length-1)*MConst.MENU_BUTTON_PADDING)/MenuButtons.values().length;
	
	private final Camera2D cam;
	private final TouchButton backButton;
	
	private double timeSinceCustomConfigCheck = 0.3;
	
	public ModeSelectScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);

		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		double yPos = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - MENU_BUTTON_HEIGHT/2;
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.setButton(new TouchButton(cam.getX(), yPos, MConst.MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			yPos = yPos - MENU_BUTTON_HEIGHT - MConst.MENU_BUTTON_PADDING;
		}
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		
		checkIfCustomConfigExists();
	}
	

	/*
	 * GLScreen methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		}
		
		//Checks if custom config exists
		timeSinceCustomConfigCheck += deltaTime;
		if(timeSinceCustomConfigCheck >= 0.5) {
			timeSinceCustomConfigCheck = 0.0;
			checkIfCustomConfigExists();
		}
		
		//Update buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.getButton().update(touchEvents);
		}
		
		
		if(MenuButtons.CLASSIC.getButton().isActivated()) {
			GameScreen.createGameBoardMaxBounds(scaler);
			BoundingRectangle bounds = GameScreen.getGameBoardMaxBounds();
			SnakiumConfig config;
			if(bounds.getWidth() <= bounds.getHeight()) {
				config = SnakiumConfigBuilder.CLASSIC_HIGH_CONFIG;
			} else {
				config = SnakiumConfigBuilder.CLASSIC_WIDE_CONFIG;
			}
			changeGLScreen(new ModelInfoScreen(this, config, "classic", "The classic version of Snake. No passing through walls, no bonus items, just Snake."));
		}
		
		else if(MenuButtons.SNAKE2.getButton().isActivated()) {
			GameScreen.createGameBoardMaxBounds(scaler);
			BoundingRectangle bounds = GameScreen.getGameBoardMaxBounds();
			SnakiumConfig config;
			if(bounds.getWidth() <= bounds.getHeight()) {
				config = SnakiumConfigBuilder.SNAKE2_HIGH_CONFIG;
			} else {
				config = SnakiumConfigBuilder.SNAKE2_WIDE_CONFIG;
			}
			changeGLScreen(new ModelInfoScreen(this, config, "snake 2", "Updated version made popular on Nokia 3310. Move through walls and get bonus items."));
		}
		
		else if(MenuButtons.SNAKIUM.getButton().isActivated()) {
			GameScreen.createGameBoardMaxBounds(scaler);
			BoundingRectangle bounds = GameScreen.getGameBoardMaxBounds();
			SnakiumConfig config;
			if(bounds.getWidth() <= bounds.getHeight()) {
				config = SnakiumConfigBuilder.SNAKIUM_HIGH_CONFIG;
			} else {
				config = SnakiumConfigBuilder.SNAKIUM_WIDE_CONFIG;
			}
			changeGLScreen(new ModelInfoScreen(this, config, "snakium", "The ultimate version of Snake. Move through walls, bonus items and increasing speed."));
		}
		
		else if(MenuButtons.CUSTOM.getButton().isActivated()) {
			CustomConfig.load();
			if(CustomConfig.isLoaded()) {
				changeGLScreen(new ModelInfoScreen(this, CustomConfig.getSnakiumConfig(), "custom", "Custom configuration loaded from skipifzero/snakium/custom.cfg."));
			}
		}
		
		else if(MenuButtons.EDITOR.getButton().isActivated()) {
			changeGLScreen(new EditorScreen(this));
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		//Render buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			SnakiumUtils.renderTouchButtonComplete(menuButton.getButton(), menuButton.toString(), MConst.MENU_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.completeDraw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, Const.FONT_COMPLETE_COLOR, SCREEN_TITLE);
	}

	private static void checkIfCustomConfigExists() {
		if(!CustomConfig.fileExists()) {
			MenuButtons.CUSTOM.getButton().disable();
		} else {
			MenuButtons.CUSTOM.getButton().enable();
		}
	}
	
	@Override
	public void resume() {
		if(Settings.music() && assets.MENU_MUSIC != null) {
			assets.MENU_MUSIC.play();
		}
	}

	@Override
	public void pause() {
		if(assets.MENU_MUSIC != null && assets.MENU_MUSIC.isPlaying()) {
			assets.MENU_MUSIC.stop();
		}
	}
	
	@Override
	public boolean catchBackKey() {
		return true;
	}
}
