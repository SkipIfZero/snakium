package com.skipifzero.snakium.screens;

import java.util.List;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.HighScores;
import com.skipifzero.snakium.HighScores.ScoreTableType;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakeScore;
import com.skipifzero.snakium.SnakiumIO;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.viewmodels.SnakiumStatsDescriptor;

public class ResultsScreen extends SnakiumGLScreen {

	private final String screenTitle;
	
	private final Camera2D cam;
	private final TouchButton menuButton, secondButton;
	
	private final boolean isGameOverScreen;
	private final SnakeScore score;
	private final boolean isNewHighScore;
	private final SnakiumStatsDescriptor statsDescriptor;
	private ScoreTableType scoreTableType = null;
	
	public ResultsScreen(SnakiumGLScreen snakiumGLScreen, ScoreTableType scoreTableType, SnakeScore score, boolean isGameOverScreen) {
		super(snakiumGLScreen);
		this.score = score;
		this.isGameOverScreen = isGameOverScreen;
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		if(isGameOverScreen) {
			this.menuButton = MConst.createLeftNavButton(cam.getX());
			this.secondButton = MConst.createRightNavButton(cam.getX());
		} else {
			this.menuButton = null;
			this.secondButton = MConst.createLeftNavButton(cam.getX());
		}
		
		if(!isGameOverScreen) {
			this.scoreTableType = scoreTableType;
			this.isNewHighScore = false;
		} else if(scoreTableType != null) {
			this.scoreTableType = scoreTableType;
			this.isNewHighScore = saveScore(scoreTableType);
		} else {
			secondButton.disable();
			isNewHighScore = false;
		}
		
		if(isGameOverScreen) {
			if(!isNewHighScore) {
				screenTitle = "game over";
			} else {
				screenTitle = "new highscore";
			}
		} else {
			screenTitle = "results";
		}
		
		double statsSize = MConst.MIN_CAM_WIDTH;
		double statsY = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_PART_HEIGHT/2;
		this.statsDescriptor = new SnakiumStatsDescriptor(cam.getX(), statsY, statsSize, Const.BORDER_WIDTH_RATIO, score.getStats());
	}
	
	private boolean saveScore(ScoreTableType type) {
		HighScores hiScores = SnakiumIO.getHighScores();
		hiScores.addScoreMaybe(type, score);
		SnakiumIO.saveHighScores(hiScores);
		return hiScores.isHighestScore(type, score);
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		if(isGameOverScreen) {
			menuButton.update(touchEvents);
			if(menuButton.isActivated() || backPressed) {
				changeGLScreen(new MainMenuScreen(this));
			}
		} else {
			if(backPressed) {
				if(scoreTableType != null) {
					changeGLScreen(new HighScoreScreen(this, scoreTableType));
				} else {
					changeGLScreen(new HighScoreScreen(this));
				}
			}
		}
		
		secondButton.update(touchEvents);
		if(secondButton.isActivated()) {
			if(scoreTableType != null) {
				changeGLScreen(new HighScoreScreen(this, scoreTableType));
			} else {
				changeGLScreen(new HighScoreScreen(this));
			}
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());

		if(isGameOverScreen) {
			SnakiumUtils.renderTouchButtonComplete(menuButton, "menu", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		}
		SnakiumUtils.renderTouchButtonComplete(secondButton, "scores", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);

		statsDescriptor.render(assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.begin(Const.FONT_COMPLETE_COLOR);
		assets.font.draw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, screenTitle);
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
