package com.skipifzero.snakium.screens;

import java.util.List;

import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumIO;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.model.SnakiumModel;


public class MainMenuScreen extends SnakiumGLScreen {
	
	private static enum MenuButtons {
		CONTINUE("continue"), 
		NEW_GAME("new game"),
		HIGH_SCORES("scores"),
		OPTIONS("options"),
		ABOUT("about");
		
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
	
	private static final double SNAKIUM_LOGO_HEIGHT = 24;
	private static final double SNAKIUM_LOGO_Y_POS = MConst.MIN_CAM_HEIGHT - MConst.TOP_TOP_PADDING - SNAKIUM_LOGO_HEIGHT/2;
	private static final double TOP_PART_HEIGHT = MConst.TOP_TOP_PADDING + SNAKIUM_LOGO_HEIGHT + MConst.TOP_BOTTOM_PADDING;
	private static final double MIDDLE_PART_HEIGHT = MConst.MIN_CAM_HEIGHT - TOP_PART_HEIGHT - MConst.BOTTOM_PART_HEIGHT;
	private static final double MIDDLE_PART_HEIGHT_MINUS_PADDING = MIDDLE_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - MConst.MIDDLE_BOTTOM_PADDING;
	
	private static final double MENU_BUTTON_HEIGHT = (MIDDLE_PART_HEIGHT_MINUS_PADDING - (MenuButtons.values().length-1)*MConst.MENU_BUTTON_PADDING)/MenuButtons.values().length;
	private static final double SKIPIFZERO_LOGO_Y_CENTER_POS = MConst.BOTTOM_BOTTOM_PADDING + MConst.BOTTOM_CONTENT_HEIGHT/2;
	
	private final Camera2D cam;

	private double timeSinceSaveCheck = 0.3;	
	
	public MainMenuScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);

		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		double yPos = MConst.MIN_CAM_HEIGHT - TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - MENU_BUTTON_HEIGHT/2;
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.setButton(new TouchButton(cam.getX(), yPos, MConst.MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			yPos = yPos - MENU_BUTTON_HEIGHT - MConst.MENU_BUTTON_PADDING;
		}
		
		if(!SnakiumIO.hasSavedSnakiumModel()) {
			MenuButtons.CONTINUE.getButton().disable();
		}
	}
	

	/*
	 * GLScreen methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		
		//Checks if a Snakium save file exists and updates continue button.
		timeSinceSaveCheck += deltaTime;
		if(timeSinceSaveCheck >= 0.5) {
			timeSinceSaveCheck = 0.0;
			if(SnakiumIO.hasSavedSnakiumModel()) {
				MenuButtons.CONTINUE.getButton().enable();
			} else {
				MenuButtons.CONTINUE.getButton().disable();
			}
		}
		
		//Update buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.getButton().update(touchEvents);
		}
		
		
		if(MenuButtons.CONTINUE.getButton().isActivated()) {
			SnakiumModel model = SnakiumIO.readSavedSnakiumModel();
			if(model != null) {
				changeGLScreen(new GameScreen(this, model, false));
			} else {
				MenuButtons.CONTINUE.getButton().disable();
			}
		}
		
		else if(MenuButtons.NEW_GAME.getButton().isActivated()) {
			changeGLScreen(new ModeSelectScreen(this));
		}
		
		else if(MenuButtons.HIGH_SCORES.getButton().isActivated()) {
			changeGLScreen(new HighScoreScreen(this));
		}
		
		else if(MenuButtons.OPTIONS.getButton().isActivated()) {
			changeGLScreen(new OptionsScreen(this));
		}
		
		else if(MenuButtons.ABOUT.getButton().isActivated()) {
			changeGLScreen(new AboutScreen(this));
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		//Render buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			SnakiumUtils.renderTouchButtonComplete(menuButton.getButton(), menuButton.toString(), MConst.MENU_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		
		//Render Snakium and SkipIfZero logos
		assets.batcher.beginBatch(assets.texAtlas1024);
		assets.batcher.draw(cam.getX(),SNAKIUM_LOGO_Y_POS, SNAKIUM_LOGO_HEIGHT*4, SNAKIUM_LOGO_HEIGHT, assets.SNAKIUM_LOGO);
		assets.batcher.draw(cam.getX(), SKIPIFZERO_LOGO_Y_CENTER_POS, MConst.BOTTOM_CONTENT_HEIGHT*4, MConst.BOTTOM_CONTENT_HEIGHT, assets.SKIPIFZERO_SNAKIUM_LOGO);
		assets.batcher.renderBatch();
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
		return false;
	}
}
