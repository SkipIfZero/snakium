package com.skipifzero.snakium.editor;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.DialogFragment;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.CustomConfig;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.model.SnakiumConfigBuilder;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;
import com.skipifzero.snakium.screens.GameScreen;
import com.skipifzero.snakium.screens.MConst;
import com.skipifzero.snakium.screens.ModeSelectScreen;
import com.skipifzero.snakium.screens.SnakiumGLScreen;
import com.skipifzero.snakium.viewmodels.SnakiumConfigDescriptor;

public class EditorScreen extends SnakiumGLScreen {
	
	private enum TabButtons {
		PAGE1("page 1", true), 
		PAGE2("page 2", false);/*,
		PREVIEW("preview", false);*/
		
		private final String name;
		private TouchButton button;
		private boolean isSelected;
		
		private TabButtons(String name, boolean isSelected) {
			this.name = name;
			this.isSelected = isSelected;
		}

		public static void setSelectedTabButton(TabButtons tabButton) {
			for(TabButtons button : TabButtons.values()) {
				button.isSelected = false;
			}
			tabButton.isSelected = true;
		}
		
		public static TabButtons getSelectedTabButton() {
			for(TabButtons tabButton : TabButtons.values()) {
				if(tabButton.isSelected) {
					return tabButton;
				}
			}
			throw new IllegalStateException("No selected TabButton.");
		}
	}
	
	private static final String SCREEN_TITLE = "custom editor";
	
	private static final int AMOUNT_OF_TAB_BUTTONS = TabButtons.values().length;
	private static final double TAB_BUTTON_EDGE_PADDING = 6;
	private static final double TAB_BUTTON_PADDING = 12;
	private static final double TAB_BUTTON_WIDTH = (MConst.MIN_CAM_WIDTH - (AMOUNT_OF_TAB_BUTTONS-1)*TAB_BUTTON_PADDING - 2*TAB_BUTTON_EDGE_PADDING)/AMOUNT_OF_TAB_BUTTONS;
	private static final double TAB_BUTTON_HEIGHT = 12;
	private static final double TAB_BUTTON_Y_POS = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - TAB_BUTTON_HEIGHT/2;
	private static final double TAB_BUTTON_TEXT_SCALING_FACTOR = 0.50;
	
	private static final double LINE_OFFSET = 8;
	private static final double LINE_PADDING = 1.25;
	
	private static final int AMOUNT_OF_MIDDLE_ROWS = 5;
	private static final double MIDDLE_TOP_PADDING = 6;
	private static final double MIDDLE_PADDING = 7;
	
	private static final double OPT_BUTTON_WIDTH = (MConst.MIN_CAM_WIDTH/2) - LINE_OFFSET - 2*LINE_PADDING;
	private static final double OPT_BUTTON_HEIGHT = (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - 2*TAB_BUTTON_EDGE_PADDING - MIDDLE_TOP_PADDING - MIDDLE_PADDING*(AMOUNT_OF_MIDDLE_ROWS-1))/AMOUNT_OF_MIDDLE_ROWS;
	private static final double OPT_BUTTON_TEXT_SCALING_FACTOR = 0.36;
	
	private static final double MIDDLE_TEXT_SIZE = OPT_BUTTON_HEIGHT*0.45;
	private static final double MIDDLE_START_Y_POS = TAB_BUTTON_Y_POS - TAB_BUTTON_HEIGHT/2 - TAB_BUTTON_EDGE_PADDING - MIDDLE_TOP_PADDING - OPT_BUTTON_HEIGHT/2;
	
	//Page 1
	private final List<TouchButton> p1_buttons = new ArrayList<TouchButton>();
	
	//Page 2
	private final List<TouchButton> p2_buttons = new ArrayList<TouchButton>();
	
	//Preview tab
	private static final double CONFIG_DESCRIPTOR_SIZE = MConst.MIN_CAM_WIDTH < (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - TAB_BUTTON_EDGE_PADDING) ? MConst.MIN_CAM_WIDTH : (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - TAB_BUTTON_EDGE_PADDING);
	private final SnakiumConfigDescriptor pre_configDescriptor;
	
	private SnakiumConfig config = null;
	
	private final Camera2D cam;
	private final TouchButton backButton, defaultsButton;
	
	public EditorScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		//Tab buttons
		double tabButtonXPos = cam.getX() - MConst.MIN_CAM_WIDTH/2 + TAB_BUTTON_EDGE_PADDING + TAB_BUTTON_WIDTH/2;
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.button = new TouchButton(tabButtonXPos, TAB_BUTTON_Y_POS, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
			tabButtonXPos += TAB_BUTTON_PADDING + TAB_BUTTON_WIDTH;
		}
		
		//Page 1
		double p1_yPos = MIDDLE_START_Y_POS;
		for(int i = 0; i < AMOUNT_OF_MIDDLE_ROWS; i++) {
			p1_buttons.add(new TouchButton(cam.getX() + OPT_BUTTON_WIDTH/2 + LINE_OFFSET + LINE_PADDING, p1_yPos, OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			p1_yPos = p1_yPos - MIDDLE_PADDING - OPT_BUTTON_HEIGHT;
		}
		
		//Page 2
		double p2_yPos = MIDDLE_START_Y_POS;
		for(int i = 0; i < AMOUNT_OF_MIDDLE_ROWS; i++) {
			p2_buttons.add(new TouchButton(cam.getX() + OPT_BUTTON_WIDTH/2 + LINE_OFFSET + LINE_PADDING, p2_yPos, OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			p2_yPos = p2_yPos - MIDDLE_PADDING - OPT_BUTTON_HEIGHT;
		}
		
		//Preview tab
		double configDescrY = MConst.BOTTOM_PART_HEIGHT + MConst.MIDDLE_BOTTOM_PADDING + (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - TAB_BUTTON_EDGE_PADDING)/2;
		this.pre_configDescriptor = new SnakiumConfigDescriptor(cam.getX(), configDescrY, CONFIG_DESCRIPTOR_SIZE, Const.BORDER_WIDTH_RATIO, SnakiumConfigBuilder.CLASSIC_BASE_CONFIG);
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		this.defaultsButton = MConst.createRightNavButton(cam.getX());
		
		CustomConfig.load();
		
		TabButtons.setSelectedTabButton(TabButtons.PAGE1);
		
		GameScreen.createGameBoardMaxBounds(scaler);
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			CustomConfig.saveToFile();
			changeGLScreen(new ModeSelectScreen(this));
		}
		
		if(!CustomConfig.isLoaded()) {
			CustomConfig.load();
		}
		
		//Update tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.button.update(touchEvents);
			if(tabButton.button.isActivated()) {
				TabButtons.setSelectedTabButton(tabButton);
			}
		}
		
		if(TabButtons.PAGE1.isSelected) {
			for(TouchButton button : p1_buttons) {
				button.update(touchEvents);
			}
			
			if(p1_buttons.get(0).isActivated()) { //board size
				DialogFragment fragment = new BoardSizeDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "boardSize");
			}
			else if(p1_buttons.get(1).isActivated()) { //pass through walls
				CustomConfig.hasWrapping(!CustomConfig.hasWrapping());
			}
			else if(p1_buttons.get(2).isActivated()) { //tiles per second
				DialogFragment fragment = new TilesPerSecondDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "tilesPerSecond");
				
			}
			else if(p1_buttons.get(3).isActivated()) { //increasing speed
				CustomConfig.hasSpeedIncrease(!CustomConfig.hasSpeedIncrease());
			}
			else if(p1_buttons.get(4).isActivated()) { //increase per object
				DialogFragment fragment = new IncreasePerObjectDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "increasePerObject");
			}
		}
		
		else if(TabButtons.PAGE2.isSelected) {
			for(TouchButton button : p2_buttons) {
				button.update(touchEvents);
			}
			
			if(p2_buttons.get(0).isActivated()) { //bonus
				CustomConfig.hasBonus(!CustomConfig.hasBonus());
			}
			else if(p2_buttons.get(1).isActivated()) { //activates after
				DialogFragment fragment = new BonusActivatesAfterDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "activatesAfter");
			}
			else if(p2_buttons.get(2).isActivated()) { //bonus duration
				DialogFragment fragment = new BonusDurationDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "bonusDuration");
			}
			else if(p2_buttons.get(3).isActivated()) { //object value
				DialogFragment fragment = new ObjectValueDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "objectValue");
			}
			else if(p2_buttons.get(4).isActivated()) { //bonus object value
				DialogFragment fragment = new BonusObjectValueDialogFragment();
				fragment.show(fragmentGLActivity.getSupportFragmentManager(), "bonusObjectValue");
			}
		}
		
//		else if(TabButtons.PREVIEW.isSelected) {
//			if(TabButtons.PREVIEW.button.isActivated()) {
//				config = CustomConfig.getSnakiumConfig();
//			}
//			pre_configDescriptor.setConfig(config);
//		}
		
		defaultsButton.update(touchEvents);
		if(defaultsButton.isActivated()) {
			DialogFragment fragment = new DefaultsDialogFragment();
			fragment.show(fragmentGLActivity.getSupportFragmentManager(), "defaults");
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		SnakiumUtils.renderTouchButtonComplete(defaultsButton, "defaults", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		//Render tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			SnakiumUtils.renderTouchButtonComplete(tabButton.button, tabButton.name, TAB_BUTTON_TEXT_SCALING_FACTOR, assets, tabButton.isSelected);
		}
		
		//Render tab specific stuff
		if(TabButtons.PAGE1.isSelected) {
			SnakiumUtils.renderTouchButtonComplete(p1_buttons.get(0), CustomConfig.boardWidth() + " x " + CustomConfig.boardHeight(), OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p1_buttons.get(1), CustomConfig.hasWrapping() ? "enabled" : "disabled", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p1_buttons.get(2), Double.toString(CustomConfig.tilesPerSecond()), OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p1_buttons.get(3), CustomConfig.hasSpeedIncrease() ? "enabled" : "disabled", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p1_buttons.get(4), CustomConfig.speedIncreasePerObject() + " t/s", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		else if(TabButtons.PAGE2.isSelected) {
			SnakiumUtils.renderTouchButtonComplete(p2_buttons.get(0), CustomConfig.hasBonus() ? "enabled" : "disabled", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p2_buttons.get(1), CustomConfig.bonusFrequency() + " objects", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p2_buttons.get(2), CustomConfig.bonusDuration() + " tiles", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p2_buttons.get(3), CustomConfig.pointsPerObject() + " pts", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
			SnakiumUtils.renderTouchButtonComplete(p2_buttons.get(4), CustomConfig.pointsPerBonusObject() + " pts", OPT_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
//		else if(TabButtons.PREVIEW.isSelected) {
//			pre_configDescriptor.render(assets);
//		}
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.begin(Const.FONT_COMPLETE_COLOR);
		assets.font.draw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, SCREEN_TITLE);
		if(TabButtons.PAGE1.isSelected) {
			assets.font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p1_buttons.get(0).getBounds().getY(), MIDDLE_TEXT_SIZE, "board size:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p1_buttons.get(1).getBounds().getY(), MIDDLE_TEXT_SIZE, "pass through walls:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p1_buttons.get(2).getBounds().getY(), MIDDLE_TEXT_SIZE, "tiles per second:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p1_buttons.get(3).getBounds().getY(), MIDDLE_TEXT_SIZE, "increasing speed:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p1_buttons.get(4).getBounds().getY(), MIDDLE_TEXT_SIZE, "increase per object:");
		}
		else if(TabButtons.PAGE2.isSelected) {
			assets.font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p2_buttons.get(0).getBounds().getY(), MIDDLE_TEXT_SIZE, "bonus:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p2_buttons.get(1).getBounds().getY(), MIDDLE_TEXT_SIZE, "activates after:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p2_buttons.get(2).getBounds().getY(), MIDDLE_TEXT_SIZE, "bonus duration:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p2_buttons.get(3).getBounds().getY(), MIDDLE_TEXT_SIZE, "object value:");
			assets.font.draw(cam.getX() + LINE_OFFSET - LINE_PADDING, p2_buttons.get(4).getBounds().getY(), MIDDLE_TEXT_SIZE, "bonus obj value:");
		}
		assets.font.render();
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
