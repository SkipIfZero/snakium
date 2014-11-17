package com.skipifzero.snakium.screens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.Settings.ScreenOrientation;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;

public class OptionsScreen extends SnakiumGLScreen {

	private static final String ON_STR = "on";
	private static final String OFF_STR = "off";
	
	private static final String ORIENT_LAYOUT_STR = "layout";
	private static final String ORIENT_PORTRAIT_STR = "portrait";
	private static final String ORIENT_LANDSCAPE_STR = "landscape";
	private static final String ORIENT_INV_PORTRAIT_STR = "inv. portr.";
	private static final String ORIENT_INV_LANDSCAPE_STR = "inv. landsc.";
	
	private enum MenuButtons {
		MUSIC("music", ON_STR, OFF_STR),
		SOUND("sound effects", ON_STR, OFF_STR),
		SHOW_BOARD("show board", ON_STR, OFF_STR),
		FLOATING_TEXTS("floating texts", ON_STR, OFF_STR),
		SHOW_FPS("show fps", ON_STR, OFF_STR),
		FULLSCREEN("fullscreen", ON_STR, OFF_STR),
		//SCREEN_ORIENTATION_LOCKED("lock orientation", ON_STR, OFF_STR), 
		SCREEN_ORIENTATION("orientation", ORIENT_LAYOUT_STR, ORIENT_PORTRAIT_STR, ORIENT_LANDSCAPE_STR, ORIENT_INV_PORTRAIT_STR, ORIENT_INV_LANDSCAPE_STR);
		
		private final String optionName;
		private String buttonText;
		private TouchButton button;
		
		private final Map<String, String> nextButtonText;
		
		private MenuButtons(String optionName, String ... buttonTexts) {
			this.optionName = optionName;
			this.nextButtonText = new HashMap<String, String>();
			
			if(buttonTexts.length <= 1) {
				throw new IllegalArgumentException();
			}
			
			for(int i = 0; i < buttonTexts.length; i++) {
				if(i == buttonTexts.length-1) {
					nextButtonText.put(buttonTexts[i], buttonTexts[0]);
				} else {
					nextButtonText.put(buttonTexts[i], buttonTexts[i+1]);
				}
			}
			
			if(!nextButtonText.keySet().containsAll(nextButtonText.values()) &&
				!nextButtonText.values().containsAll(nextButtonText.keySet()) ) {
				throw new IllegalArgumentException();
			}
		}
		
		public void setButtonText(String buttonText) {
			if(!nextButtonText.keySet().contains(buttonText)) {
				throw new IllegalArgumentException();
			}
			this.buttonText = buttonText;
		}
		
		public String getButtonText() {
			return buttonText;
		}
		
		public void nextButtonText() {
			this.buttonText = nextButtonText.get(buttonText);
		}
		
		public void setButton(TouchButton button) {
			this.button = button;
		}
		
		public TouchButton getButton() {
			return button;
		}
		
		public String getOptionName() {
			return optionName;
		}
	}
	
	
	private static final String SCREEN_TITLE = "options";
	
	private static final double LINE_SPACING = 2;
	private static final double LINE_OFFSET = 5;
	
	private static final double OPT_BUTTON_PADDING = 4.5;
	private static final double OPT_BUTTON_WIDTH = (MConst.MIN_CAM_WIDTH/2) - LINE_OFFSET - 2*LINE_SPACING;
	private static final double OPT_BUTTON_HEIGHT = (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - (MenuButtons.values().length-1)*OPT_BUTTON_PADDING)/MenuButtons.values().length;
	private static final double OPT_BUTTON_TEXT_SCALING_FACTOR = 0.48;
	
	private static final double OPTION_TEXT_SIZE = OPT_BUTTON_HEIGHT*OPT_BUTTON_TEXT_SCALING_FACTOR*1.15;
	
	
	private final Camera2D cam;
	private final TouchButton backButton, defaultsButton;
	
	private boolean restartActivity = false;
	private boolean settingsChanged = false;
	
	public OptionsScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		//Creates option TouchButtons
		double yPos = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - OPT_BUTTON_HEIGHT/2;
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.setButton(new TouchButton(cam.getX() + LINE_OFFSET + LINE_SPACING + OPT_BUTTON_WIDTH/2, yPos, OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			yPos = yPos - OPT_BUTTON_HEIGHT - OPT_BUTTON_PADDING;
		}
		
		setButtonStringsFromSettings();
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		this.defaultsButton = MConst.createRightNavButton(cam.getX());
		
		defaultsButtonStateCheck();
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backPressed || backButton.isActivated()) {
			if(settingsChanged) {
				Settings.save();
			}
			if(restartActivity) {
				restartActivity();
			} else {
				changeGLScreen(new MainMenuScreen(this));
			}
		}
		
		//Update buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			menuButton.getButton().update(touchEvents);
		}
		defaultsButton.update(touchEvents);
		
		if(MenuButtons.MUSIC.getButton().isActivated()) {
			MenuButtons.MUSIC.nextButtonText();
			if(MenuButtons.MUSIC.getButtonText().equals(ON_STR)) {
				Settings.music(true);
			} else {
				Settings.music(false);
			}
			settingsChanged = true;
			defaultsButtonStateCheck();
		}
		else if(MenuButtons.SOUND.getButton().isActivated()) {
			MenuButtons.SOUND.nextButtonText();
			if(MenuButtons.SOUND.getButtonText().equals(ON_STR)) {
				Settings.sound(true);
			} else {
				Settings.sound(false);
			}
			settingsChanged = true;
			defaultsButtonStateCheck();
		}
		else if(MenuButtons.SHOW_BOARD.getButton().isActivated()) {
			MenuButtons.SHOW_BOARD.nextButtonText();
			if(MenuButtons.SHOW_BOARD.getButtonText().equals(ON_STR)) {
				Settings.showBoard(true);
			} else {
				Settings.showBoard(false);
			}
			settingsChanged = true;
			defaultsButtonStateCheck();
		}
		else if(MenuButtons.FLOATING_TEXTS.getButton().isActivated()) {
			MenuButtons.FLOATING_TEXTS.nextButtonText();
			if(MenuButtons.FLOATING_TEXTS.getButtonText().equals(ON_STR)) {
				Settings.floatingTexts(true);
			} else {
				Settings.floatingTexts(false);
			}
			settingsChanged = true;
			defaultsButtonStateCheck();
		}
		else if(MenuButtons.SHOW_FPS.getButton().isActivated()) {
			MenuButtons.SHOW_FPS.nextButtonText();
			if(MenuButtons.SHOW_FPS.getButtonText().equals(ON_STR)) {
				Settings.showFPS(true);
			} else {
				Settings.showFPS(false);
			}
			settingsChanged = true;
			defaultsButtonStateCheck();
		}
		else if(MenuButtons.FULLSCREEN.getButton().isActivated()) {
			MenuButtons.FULLSCREEN.nextButtonText();
			if(MenuButtons.FULLSCREEN.getButtonText().equals(ON_STR)) {
				Settings.fullscreen(true);
			} else {
				Settings.fullscreen(false);
			}
			settingsChanged = true;
			restartActivity = true;
			defaultsButtonStateCheck();
		}
//		else if(MenuButtons.SCREEN_ORIENTATION_LOCKED.getButton().isActivated()) {
//			MenuButtons.SCREEN_ORIENTATION_LOCKED.nextButtonText();
//			if(MenuButtons.SCREEN_ORIENTATION_LOCKED.getButtonText().equals(ON_STR)) {
//				Settings.screenOrientationLocked(true);
//				MenuButtons.SCREEN_ORIENTATION.getButton().enable();
//			} else {
//				Settings.screenOrientationLocked(false);
//				MenuButtons.SCREEN_ORIENTATION.getButton().disable();
//			}
//			settingsChanged = true;
//			restartActivity = true;
//			defaultsButtonStateCheck();
//		}
		else if(MenuButtons.SCREEN_ORIENTATION.getButton().isActivated()) {
			MenuButtons.SCREEN_ORIENTATION.nextButtonText();
			String temp = MenuButtons.SCREEN_ORIENTATION.getButtonText();
			if(temp.equals(ORIENT_LAYOUT_STR)) {
				Settings.screenOrientation(ScreenOrientation.LAYOUT);
			} else if(temp.equals(ORIENT_PORTRAIT_STR)) {
				Settings.screenOrientation(ScreenOrientation.PORTRAIT);
			} else if(temp.equals(ORIENT_LANDSCAPE_STR)) {
				Settings.screenOrientation(ScreenOrientation.LANDSCAPE);
			} else if(temp.equals(ORIENT_INV_PORTRAIT_STR)) {
				Settings.screenOrientation(ScreenOrientation.PORTRAIT_REVERSE);
			} else {
				Settings.screenOrientation(ScreenOrientation.LANDSCAPE_REVERSE);
			}
			settingsChanged = true;
			restartActivity = true;
			defaultsButtonStateCheck();
		}
		
		if(defaultsButton.isActivated()) {
			if(!Settings.isDefaults()) {
				Settings.setDefaults();
				setButtonStringsFromSettings();
				settingsChanged = true;
				restartActivity = true;
				defaultsButton.disable();
			}
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		//Render buttons
		for(MenuButtons menuButton : MenuButtons.values()) {
			TouchButton button = menuButton.getButton();
			assets.font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
			assets.font.completeDraw(cam.getX() + LINE_OFFSET - LINE_SPACING, button.getPosition().getY(), OPTION_TEXT_SIZE, Const.FONT_COMPLETE_COLOR, menuButton.getOptionName());
			SnakiumUtils.renderTouchButtonComplete(menuButton.getButton(), menuButton.getButtonText(), OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		
		if(!settingsChanged) {
			SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		} else {
			SnakiumUtils.renderTouchButtonComplete(backButton, "apply", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		
		SnakiumUtils.renderTouchButtonComplete(defaultsButton, "default", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.completeDraw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, Const.FONT_COMPLETE_COLOR, SCREEN_TITLE);
	}

	private void defaultsButtonStateCheck() {
		if(Settings.isDefaults()) {
			defaultsButton.disable();
		} else {
			defaultsButton.enable();
		}
	}
	
	private static void setButtonStringsFromSettings() {
		MenuButtons.MUSIC.setButtonText(Settings.music() ? ON_STR : OFF_STR);
		MenuButtons.SOUND.setButtonText(Settings.sound() ? ON_STR : OFF_STR);
		MenuButtons.SHOW_BOARD.setButtonText(Settings.showBoard() ? ON_STR : OFF_STR);
		MenuButtons.FLOATING_TEXTS.setButtonText(Settings.floatingTexts() ? ON_STR : OFF_STR);
		MenuButtons.SHOW_FPS.setButtonText(Settings.showFPS() ? ON_STR : OFF_STR);
		MenuButtons.FULLSCREEN.setButtonText(Settings.fullscreen() ? ON_STR : OFF_STR);
		//MenuButtons.SCREEN_ORIENTATION_LOCKED.setButtonText(Settings.screenOrientationLocked() ? ON_STR : OFF_STR);
		String temp;
		switch(Settings.screenOrientation()) {
			case LAYOUT:
				temp = ORIENT_LAYOUT_STR;
				break;
			case PORTRAIT:
				temp = ORIENT_PORTRAIT_STR;
				break;
			case LANDSCAPE:
				temp = ORIENT_LANDSCAPE_STR;
				break;
			case PORTRAIT_REVERSE:
				temp = ORIENT_INV_PORTRAIT_STR;
				break;
			case LANDSCAPE_REVERSE:
				temp = ORIENT_INV_LANDSCAPE_STR;
				break;
			default:
				throw new AssertionError();
		}
		MenuButtons.SCREEN_ORIENTATION.setButtonText(temp);
		
//		if(!Settings.screenOrientationLocked()) {
//			MenuButtons.SCREEN_ORIENTATION.getButton().disable();
//		} else {
//			MenuButtons.SCREEN_ORIENTATION.getButton().enable();
//		}
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
