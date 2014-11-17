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
import com.skipifzero.snakium.model.SnakiumConfigBuilder;
import com.skipifzero.snakium.model.SnakiumModel;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;
import com.skipifzero.snakium.viewmodels.SnakiumStatsDescriptor;

public class GameOverScreen extends SnakiumGLScreen {
	private final String screenTitle;
	
	private final Camera2D cam;
	private final TouchButton menuButton, replayButton;
	
	private final SnakiumConfig config;
	private final SnakeScore score;
	private final boolean snakeAI;
	private final boolean isNewHighScore;
	private final SnakiumStatsDescriptor statsDescriptor;
	private final ScoreTableType scoreTableType;
	
	public GameOverScreen(SnakiumGLScreen snakiumGLScreen, SnakiumConfig config, SnakeScore score, boolean snakeAI) {
		super(snakiumGLScreen);
		this.config = config;
		if(config.id == SnakiumConfigBuilder.CLASSIC_CONFIG_ID) {
			this.scoreTableType = ScoreTableType.CLASSIC;
		} else if(config.id == SnakiumConfigBuilder.SNAKE2_CONFIG_ID) {
			this.scoreTableType = ScoreTableType.SNAKE2;
		} else if(config.id == SnakiumConfigBuilder.SNAKIUM_CONFIG_ID) {
			this.scoreTableType = ScoreTableType.SNAKIUM;
		} else {
			this.scoreTableType = null;
		}
		this.score = score;
		this.snakeAI = snakeAI;
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		this.menuButton = MConst.createLeftNavButton(cam.getX());
		this.replayButton = MConst.createRightNavButton(cam.getX());
		this.replayButton.setWidth(MConst.NAV_BUTTON_WIDTH+4);
		this.replayButton.getPosition().setX(replayButton.getPosition().getX() + 2);

		if(scoreTableType != null) {
			this.isNewHighScore = saveScore(scoreTableType);
		} else {
			isNewHighScore = false;
		}
		
		if(!isNewHighScore) {
			screenTitle = "game over";
		} else {
			screenTitle = "new highscore";
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
		menuButton.update(touchEvents);
		if(menuButton.isActivated() || backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		}
		
		replayButton.update(touchEvents);
		if(replayButton.isActivated()) {
			changeGLScreen(new GameScreen(this, new SnakiumModel(config), snakeAI));
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());

		SnakiumUtils.renderTouchButtonComplete(menuButton, "menu", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		SnakiumUtils.renderTouchButtonComplete(replayButton, "play again", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR*0.85, assets);

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
