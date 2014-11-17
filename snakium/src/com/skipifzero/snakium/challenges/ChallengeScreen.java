package com.skipifzero.snakium.challenges;

import java.util.ArrayList;
import java.util.List;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakeScore;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.screens.HighScoreScreen;
import com.skipifzero.snakium.screens.MConst;
import com.skipifzero.snakium.screens.MainMenuScreen;
import com.skipifzero.snakium.screens.SnakiumGLScreen;
import com.skipifzero.snakium.viewmodels.SnakeScoreDescriptor;

public class ChallengeScreen extends SnakiumGLScreen {
	
	private enum TabButtons {
		PAGE1("page 1", true), 
		PAGE2("page 2", false),
		PAGE3("page 3", false);
		
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
	
	private static final String SCREEN_TITLE = "challenges";
	
	private static final int AMOUNT_OF_ROWS = 5;
	
	private static final double TAB_BUTTON_HEIGHT = 12;
	private static final double TAB_BUTTON_EDGE_PADDING = 2;
	private static final double TAB_BUTTON_PADDING = 3;
	private static final double TAB_BUTTON_Y_POS = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - TAB_BUTTON_HEIGHT/2;
	private static final double TAB_BUTTON_TEXT_SCALING_FACTOR = 0.50;
	
	private static final double SCORE_DESCR_TOP_PADDING = 5;
	private static final double SCORE_DESCR_PADDING = 6;
	private static final double SCORE_DESCR_HEIGHT = (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - 2*TAB_BUTTON_EDGE_PADDING - SCORE_DESCR_TOP_PADDING - SCORE_DESCR_PADDING*(AMOUNT_OF_ROWS-1))/AMOUNT_OF_ROWS;
	private static final double SCORE_DESCR_START_Y_POS = TAB_BUTTON_Y_POS - TAB_BUTTON_HEIGHT/2 - TAB_BUTTON_EDGE_PADDING - SCORE_DESCR_TOP_PADDING - SCORE_DESCR_HEIGHT/2;
	private static final double SCORE_DESCR_BORDER_WIDTH_RATIO = Const.BORDER_WIDTH_RATIO;
	
	private static final double SCORE_BUTTON_DESCR_PADDING = 0;
	private static final double SCORE_BUTTON_WIDTH = SCORE_DESCR_HEIGHT;
	private static final double SCORE_BUTTON_HEIGHT = SCORE_DESCR_HEIGHT*0.7;
	private static final double SCORE_BUTTON_TEXT_SCALING_FACTOR = 0.55;
	
	private final Camera2D cam;
	private final TouchButton backButton, scoreButton;
	private final List<SnakeScoreDescriptor> scoreDescriptors = new ArrayList<SnakeScoreDescriptor>(AMOUNT_OF_ROWS);
	private final List<TouchButton> scoreButtons = new ArrayList<TouchButton>();
	

	public ChallengeScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		//Tab buttons
		int amountOfTabButtons = TabButtons.values().length;
		double tabButtonWidth = (MConst.MIN_CAM_WIDTH - (amountOfTabButtons-1)*TAB_BUTTON_PADDING - 2*TAB_BUTTON_EDGE_PADDING)/amountOfTabButtons;
		double tabButtonXPos = cam.getX() - MConst.MIN_CAM_WIDTH/2 + TAB_BUTTON_EDGE_PADDING + tabButtonWidth/2;
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.button = new TouchButton(tabButtonXPos, TAB_BUTTON_Y_POS, tabButtonWidth, TAB_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
			tabButtonXPos += TAB_BUTTON_PADDING + tabButtonWidth;
		}
		
		double scoreDescrXPos = cam.getX() - (SCORE_DESCR_HEIGHT*3 + SCORE_BUTTON_DESCR_PADDING + SCORE_BUTTON_WIDTH)/2 + (SCORE_DESCR_HEIGHT*3)/2;
		double scoreButtonXPos = cam.getX() + (SCORE_DESCR_HEIGHT*3 + SCORE_BUTTON_DESCR_PADDING + SCORE_BUTTON_WIDTH)/2 - SCORE_BUTTON_WIDTH/2;
		double scoreYPos = SCORE_DESCR_START_Y_POS;
		for(int i = 0; i < AMOUNT_OF_ROWS; i++)  {
			scoreDescriptors.add(new SnakeScoreDescriptor(scoreDescrXPos, scoreYPos, SCORE_DESCR_HEIGHT, SCORE_DESCR_BORDER_WIDTH_RATIO, null));
			scoreButtons.add(new TouchButton(scoreButtonXPos, scoreYPos, SCORE_BUTTON_WIDTH, SCORE_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			scoreYPos = scoreYPos - SCORE_DESCR_HEIGHT - SCORE_DESCR_PADDING;
		}
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		this.scoreButton = MConst.createRightNavButton(cam.getX());
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		}
		
		scoreButton.update(touchEvents);
		if(scoreButton.isActivated()) {
			changeGLScreen(new HighScoreScreen(this));
		}
		
		//Update tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.button.update(touchEvents);
			if(tabButton.button.isActivated()) {
				TabButtons.setSelectedTabButton(tabButton);
			}
		}
		
		//Update ScoreButtons
		for(int i = 0; i < scoreButtons.size(); i++) {
			scoreButtons.get(i).update(touchEvents);
			if(scoreButtons.get(i).isActivated()) {
				SnakeScore score = scoreDescriptors.get(i).getScore();
				if(score != null) {
					//changeGLScreen(new ResultsScreen(this, currentType, score, false));
				}
			}
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		SnakiumUtils.renderTouchButtonComplete(scoreButton, "scores", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		//Render tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			SnakiumUtils.renderTouchButtonComplete(tabButton.button, tabButton.name, TAB_BUTTON_TEXT_SCALING_FACTOR, assets, tabButton.isSelected);
		}
		
		//Render SnakeScoreDescriptors
		for(int i = 0; i < scoreDescriptors.size(); i++) {
			scoreDescriptors.get(i).render(assets);
			if(scoreDescriptors.get(i).isVisible()) {
				SnakiumUtils.renderTouchButtonComplete(scoreButtons.get(i), "->", SCORE_BUTTON_TEXT_SCALING_FACTOR, assets);
			}
		}
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.completeDraw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, Const.FONT_COMPLETE_COLOR, SCREEN_TITLE);
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
