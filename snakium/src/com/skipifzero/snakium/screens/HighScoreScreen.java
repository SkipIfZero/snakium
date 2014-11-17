package com.skipifzero.snakium.screens;

import java.util.ArrayList;
import java.util.List;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.HighScores;
import com.skipifzero.snakium.HighScores.ScoreTableType;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakeScore;
import com.skipifzero.snakium.SnakiumIO;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.challenges.ChallengeScreen;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.viewmodels.SnakeScoreDescriptor;

public class HighScoreScreen extends SnakiumGLScreen {

	private static final int AMOUNT_OF_SHOWN_SCORES = 5;
	
	private enum TabButtons {
		SNAKIUM("snakium", ScoreTableType.SNAKIUM), 
		SNAKE2("snake 2", ScoreTableType.SNAKE2),
		CLASSIC("classic", ScoreTableType.CLASSIC);
		
		private final String name;
		private final ScoreTableType tableType;
		private TouchButton button;
		private List<SnakeScore> scores = new ArrayList<SnakeScore>(AMOUNT_OF_SHOWN_SCORES);
		
		private TabButtons(String name, ScoreTableType tableType) {
			this.name = name;
			this.tableType = tableType;
		}
		
		public void setButton(TouchButton button) {
			this.button = button;
		}
		
		public TouchButton getButton() {
			return button;
		}
		
		public void loadScores(HighScores highScores) {
			this.scores.clear();
			this.scores.addAll(highScores.getSnakeScoreTable(tableType));
		}
		
		public List<SnakeScore> getScores() {
			return scores;
		}
		
		public String getName() {
			return name;
		}
		
		public ScoreTableType getScoreTableType() {
			return tableType;
		}
		
		public static TabButtons getTabButton(ScoreTableType tableType) {
			for(TabButtons tabButton : TabButtons.values()) {
				if(tabButton.tableType == tableType) {
					return tabButton;
				}
			}
			throw new IllegalArgumentException("No button with chosen ScoreTableType exists");
		}
	}
	
	private static final String SCREEN_TITLE = "high scores";
	
	private static final double TAB_BUTTON_HEIGHT = 12;
	private static final double TAB_BUTTON_EDGE_PADDING = 2;
	private static final double TAB_BUTTON_PADDING = 3;
	private static final double TAB_BUTTON_Y_POS = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - TAB_BUTTON_HEIGHT/2;
	private static final double TAB_BUTTON_TEXT_SCALING_FACTOR = 0.50;
	
	private static final double SCORE_DESCR_TOP_PADDING = 5;
	private static final double SCORE_DESCR_PADDING = 6;
	private static final double SCORE_DESCR_HEIGHT = (MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - TAB_BUTTON_HEIGHT - 2*TAB_BUTTON_EDGE_PADDING - SCORE_DESCR_TOP_PADDING - SCORE_DESCR_PADDING*(AMOUNT_OF_SHOWN_SCORES-1))/AMOUNT_OF_SHOWN_SCORES;
	private static final double SCORE_DESCR_START_Y_POS = TAB_BUTTON_Y_POS - TAB_BUTTON_HEIGHT/2 - TAB_BUTTON_EDGE_PADDING - SCORE_DESCR_TOP_PADDING - SCORE_DESCR_HEIGHT/2;
	private static final double SCORE_DESCR_BORDER_WIDTH_RATIO = Const.BORDER_WIDTH_RATIO;
	
	private static final double SCORE_BUTTON_DESCR_PADDING = 0;
	private static final double SCORE_BUTTON_WIDTH = SCORE_DESCR_HEIGHT;
	private static final double SCORE_BUTTON_HEIGHT = SCORE_DESCR_HEIGHT*0.7;
	private static final double SCORE_BUTTON_TEXT_SCALING_FACTOR = 0.55;
	
	private final Camera2D cam;
	private final TouchButton backButton, challengeButton;
	private final List<SnakeScoreDescriptor> scoreDescriptors = new ArrayList<SnakeScoreDescriptor>(AMOUNT_OF_SHOWN_SCORES);
	private final List<TouchButton> scoreButtons = new ArrayList<TouchButton>();
	private ScoreTableType currentType;
	
	public HighScoreScreen(SnakiumGLScreen snakiumGLScreen) {
		this(snakiumGLScreen, TabButtons.values()[0].getScoreTableType());
	}
	
	public HighScoreScreen(SnakiumGLScreen snakiumGLScreen, ScoreTableType defaultType) {
		super(snakiumGLScreen);
		this.currentType = defaultType;
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		//Tab buttons
		HighScores highScores = SnakiumIO.getHighScores();
		int amountOfTabButtons = TabButtons.values().length;
		double tabButtonWidth = (MConst.MIN_CAM_WIDTH - (amountOfTabButtons-1)*TAB_BUTTON_PADDING - 2*TAB_BUTTON_EDGE_PADDING)/amountOfTabButtons;
		double tabButtonXPos = cam.getX() - MConst.MIN_CAM_WIDTH/2 + TAB_BUTTON_EDGE_PADDING + tabButtonWidth/2;
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.loadScores(highScores);
			tabButton.setButton(new TouchButton(tabButtonXPos, TAB_BUTTON_Y_POS, tabButtonWidth, TAB_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			tabButtonXPos += TAB_BUTTON_PADDING + tabButtonWidth;
		}
		
		double scoreDescrXPos = cam.getX() - (SCORE_DESCR_HEIGHT*3 + SCORE_BUTTON_DESCR_PADDING + SCORE_BUTTON_WIDTH)/2 + (SCORE_DESCR_HEIGHT*3)/2;
		double scoreButtonXPos = cam.getX() + (SCORE_DESCR_HEIGHT*3 + SCORE_BUTTON_DESCR_PADDING + SCORE_BUTTON_WIDTH)/2 - SCORE_BUTTON_WIDTH/2;
		double scoreYPos = SCORE_DESCR_START_Y_POS;
		for(int i = 0; i < AMOUNT_OF_SHOWN_SCORES; i++)  {
			scoreDescriptors.add(new SnakeScoreDescriptor(scoreDescrXPos, scoreYPos, SCORE_DESCR_HEIGHT, SCORE_DESCR_BORDER_WIDTH_RATIO, null));
			scoreButtons.add(new TouchButton(scoreButtonXPos, scoreYPos, SCORE_BUTTON_WIDTH, SCORE_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE));
			scoreYPos = scoreYPos - SCORE_DESCR_HEIGHT - SCORE_DESCR_PADDING;
		}
		setScores(TabButtons.getTabButton(currentType));
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		this.challengeButton = MConst.createRightNavButton(cam.getX());
		this.challengeButton.setWidth(MConst.NAV_BUTTON_WIDTH+5);
		this.challengeButton.getPosition().setX(challengeButton.getPosition().getX() + 2.5);
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		}
		
		//Update tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			tabButton.getButton().update(touchEvents);
			if(tabButton.getButton().isActivated()) {
				currentType = tabButton.getScoreTableType();
				setScores(tabButton);
			}
		}
		
		//Update ScoreButtons
		for(int i = 0; i < scoreButtons.size(); i++) {
			scoreButtons.get(i).update(touchEvents);
			if(scoreButtons.get(i).isActivated()) {
				SnakeScore score = scoreDescriptors.get(i).getScore();
				if(score != null) {
					changeGLScreen(new ResultsScreen(this, currentType, score, false));
				}
			}
		}
		
//		challengeButton.update(touchEvents);
//		if(challengeButton.isActivated()) {
//			changeGLScreen(new ChallengeScreen(this));
//		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
//		SnakiumUtils.renderTouchButtonComplete(challengeButton, "challenges", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR*0.9, assets);
		
		//Render tab buttons
		for(TabButtons tabButton : TabButtons.values()) {
			SnakiumUtils.renderTouchButtonComplete(tabButton.getButton(), tabButton.getName(), TAB_BUTTON_TEXT_SCALING_FACTOR, assets, tabButton.getScoreTableType() == currentType);
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
	
	private void setScores(TabButtons tabButton) {
		for(SnakeScoreDescriptor descriptor : scoreDescriptors) {
			descriptor.setScore(null);
		}
		List<SnakeScore> scores = tabButton.getScores();
		for(int i = 0; i < scores.size() && i < AMOUNT_OF_SHOWN_SCORES; i++) {
			scoreDescriptors.get(i).setScore(scores.get(i));
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
