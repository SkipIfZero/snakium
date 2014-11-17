package com.skipifzero.snakium.screens;

import java.util.List;

import android.util.Log;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakeScore;
import com.skipifzero.snakium.SnakiumIO;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.TouchController;
import com.skipifzero.snakium.framework.DisplayScaling;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.math.Vector2;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.model.SnakeDirection;
import com.skipifzero.snakium.model.SnakeTile;
import com.skipifzero.snakium.model.SnakiumModel;
import com.skipifzero.snakium.viewmodels.SnakiumModelDescriptor;

public class GameScreen extends SnakiumGLScreen {

	private static BoundingRectangle maxGameBoardBounds = null;
	
	
	private static final double GAMEBOARD_BORDER_WIDTH_RATIO = 1.0/128.0;
	
	private static final double HUD_HEIGHT = 25.0;
	private static final double HUD_MIN_WIDTH = HUD_HEIGHT*12;
	private static final double BONUS_TIME_HEIGHT = HUD_HEIGHT - 2;
	
	private static final double PAUSE_BUTTON_HEIGHT = HUD_HEIGHT - 2;
	private static final double PAUSE_BUTTON_WIDTH = PAUSE_BUTTON_HEIGHT*3.2;
	private static final double PAUSE_BUTTON_TEXT_SCALING_FACTOR = 0.8;

	private static final double SCORE_TEXT_SIZE = HUD_HEIGHT - 2;
	
	private static final double FPS_COUNTER_SIZE = HUD_HEIGHT*0.5;
	
	private static final double PAUSE_DELAY = 0.5;
	private static final double GAME_OVER_DELAY = 2.0;
	
	
	private final Camera2D cam;
	
	private final SnakiumModel model;
	private final boolean snakeAIEnabled;
	
	//Controls
	private final TouchButton pauseButton;
	private final TouchController touchController;
	
	//Positions
	private final Vector2 fpsPos, scorePos;
	
	//NumberBuilders
	private final NumberBuilder scoreBuilder = new NumberBuilder(0);
	private final NumberBuilder bonusTimeBuilder = new NumberBuilder(0);
	
	private final TouchButton bonusTimeHackButton;
	
	//Views
	private final SnakiumModelDescriptor gameBoard;
	
	//Variables
	private boolean justResumed;
	private boolean paused = true;
	private double pauseTime = 0.0;
	private double gameOverTime = 0.0;
	private final Vector2 touchDiffVector = new Vector2(0,0);
	private final Vector2 touchDiffLinePos = new Vector2(0,0);
	
	public GameScreen(SnakiumGLScreen snakiumGLScreen, SnakiumModel snakiumModel, boolean snakeAIEnabled) {
		super(snakiumGLScreen);
		this.model = snakiumModel;
		this.snakeAIEnabled = snakeAIEnabled;
		
		if(scaler.getViewWidthDps() < HUD_MIN_WIDTH) {
			double height = (HUD_MIN_WIDTH*scaler.getViewHeightDps())/scaler.getViewWidthDps();
			this.cam = new Camera2D(HUD_MIN_WIDTH/2, height/2, HUD_MIN_WIDTH, height);
		} else {
			this.cam = new Camera2D(scaler.getViewWidthDps()/2, scaler.getViewHeightDps()/2, scaler.getViewWidthDps(), scaler.getViewHeightDps());
		}
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		this.gameBoard = new SnakiumModelDescriptor(cam.getX(), (cam.getHeight() - HUD_HEIGHT)/2, cam.getWidth(), cam.getHeight() - HUD_HEIGHT, GAMEBOARD_BORDER_WIDTH_RATIO, model); 
		
		double hudWidth = gameBoard.getWidth();
		if(hudWidth < HUD_MIN_WIDTH) {
			hudWidth = HUD_MIN_WIDTH;
		}

		//Controls
		this.pauseButton = new TouchButton(cam.getX() - hudWidth/2 + (PAUSE_BUTTON_WIDTH)/2, cam.getHeight() - HUD_HEIGHT/2, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
		this.touchController = new TouchController();
		
		//Positions
		this.fpsPos = new Vector2(pauseButton.getBounds().getX() + pauseButton.getWidth()/2, cam.getHeight());
		this.scorePos = new Vector2(cam.getX() + hudWidth/2, cam.getHeight());
		
		//HackButton
		this.bonusTimeHackButton = new TouchButton(cam.getX(), cam.getHeight() - HUD_HEIGHT/2, BONUS_TIME_HEIGHT*2.4, BONUS_TIME_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
	}

	/*
	 * GLScreen methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		if(justResumed) {
			justResumed = false;
			return;
		}
		
		if(backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		} 
		
		if(model.isGameOver()) {
			SnakiumIO.deleteSavedSnakiumModel();
			gameOverTime += deltaTime;
			if(gameOverTime >= GAME_OVER_DELAY) {
				changeGLScreen(new GameOverScreen(this, model.getConfig(), new SnakeScore(model.getStats()), snakeAIEnabled));
			}
		}
		
		if(touchEvents.size() > 0 && pauseTime >= PAUSE_DELAY) {
			paused = false;
		}
		
		if(paused) {
			pauseTime += deltaTime;
			return;
		}
		
		//Updates menubutton and controller.
		pauseButton.update(touchEvents);
		if(pauseButton.isActivated()) {
			paused = true;
			pauseTime = 0.0;
			return;
		}
		
		SnakeDirection direction = touchController.getSnakeDirection(touchEvents);
		
		//Get direction from AI if AI is enabled.
		if(snakeAIEnabled) {
			direction = snakeAI();
		}
		
		model.update(deltaTime, direction);
		gameBoard.update(deltaTime);
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		//Initialize camera
		cam.initialize(getViewWidth(), getViewHeight());
		
		gameBoard.render(assets);
		
		assets.batcher.beginBatch(assets.texAtlas128);
		
		//Render SnakeController
		if(touchController.isActive()) {
			touchDiffVector.set(touchController.getCurrentPosition()).sub(touchController.getStartPosition());
			touchDiffLinePos.set(touchDiffVector).mult(0.5).add(touchController.getStartPosition());
			
			assets.batcher.draw(touchDiffLinePos, touchDiffVector.getLength(), 15, touchDiffVector.getAngle(), assets.touchLineRegion);
			
			assets.batcher.draw(touchController.getStartPosition(), 20, 20, assets.touchStartRegion);
			assets.batcher.draw(touchController.getCurrentPosition(), 20, 20, assets.touchCurrentRegion);
		}

		assets.batcher.renderBatch();
		
		//Draws menu button
		SnakiumUtils.renderTouchButtonComplete(pauseButton, "pause", PAUSE_BUTTON_TEXT_SCALING_FACTOR, assets, paused);

		assets.font.begin(Const.FONT_COMPLETE_COLOR);
		
		//Print fps
		if(Settings.showFPS()) {
			assets.font.setHorizontalAlignment(HorizontalAlignment.LEFT);
			assets.font.setVerticalAlignment(VerticalAlignment.TOP);
			assets.font.draw(fpsPos, FPS_COUNTER_SIZE, fpsBuilder.toString());
		}
		
		//Print score
		scoreBuilder.update(model.getScore());
		assets.font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		assets.font.setVerticalAlignment(VerticalAlignment.TOP);
		assets.font.draw(scorePos, SCORE_TEXT_SIZE, scoreBuilder.toString());
		
		assets.font.render();
		
		if(model.bonusTimeLeft() > 0) {
			bonusTimeBuilder.update(model.bonusTimeLeft());
			SnakiumUtils.renderTouchButtonComplete(bonusTimeHackButton, bonusTimeBuilder.toString(), 0.95, assets, true);
		}
	}
	
	public static void createGameBoardMaxBounds(DisplayScaling scaler) {
		BoundingRectangle cam;
		if(scaler.getViewWidthDps() < HUD_MIN_WIDTH) {
			double height = (HUD_MIN_WIDTH*scaler.getViewHeightDps())/scaler.getViewWidthDps();
			cam = new BoundingRectangle(HUD_MIN_WIDTH/2, height/2, HUD_MIN_WIDTH, height);
		} else {
			cam = new BoundingRectangle(scaler.getViewWidthDps()/2, scaler.getViewHeightDps()/2, scaler.getViewWidthDps(), scaler.getViewHeightDps());
		}
		
		GameScreen.maxGameBoardBounds = new BoundingRectangle(cam.getX(), (cam.getHeight() - HUD_HEIGHT)/2, cam.getWidth(), cam.getHeight() - HUD_HEIGHT);
	}
	
	public static BoundingRectangle getGameBoardMaxBounds() {
		return maxGameBoardBounds;
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private SnakeDirection snakeAI() {
		SnakeTile snakeHead = null;
		for(SnakeTile tile : model.getSnakeTiles()) {
			if(tile.getType() == SnakeTile.Type.SNAKE_HEAD) {
				snakeHead = tile;
				break;
			}
		}

		if(snakeHead == null) {
			return SnakeDirection.NONE;
		}

		SnakeDirection direction = SnakeDirection.NONE;
		switch(snakeHead.getFromDirection()) {
		case UP:
			if(snakeHead.getY() == 0) {
				direction = SnakeDirection.RIGHT;
			}
			break;
		case DOWN:
			if(snakeHead.getY() == model.getBoardHeight() - 1) {
				direction = SnakeDirection.RIGHT;
			}
			break;
		case LEFT:
			if(snakeHead.getY() == 0) {
				direction = SnakeDirection.UP;
			} else {
				direction = SnakeDirection.DOWN;
			}
			break;
		case RIGHT:
		case NONE:
		default:
			throw new AssertionError();
		}
		return direction;
	}

	@Override
	public boolean catchBackKey() {
		return true;
	}

	@Override
	public void resume() {
		justResumed = true;
		if(Settings.music() && assets.GAME_MUSIC != null) {
			assets.GAME_MUSIC.play();
			assets.GAME_MUSIC.setPosition(0);
			assets.GAME_MUSIC.setLooping(true);
		}
	}

	@Override
	public void pause() {
		if(assets.GAME_MUSIC != null && assets.GAME_MUSIC.isPlaying()) {
			assets.GAME_MUSIC.stop();
		}
		if(!model.isGameOver() && !snakeAIEnabled) {
			SnakiumIO.saveSnakiumModel(model);
		} else {
			SnakiumIO.deleteSavedSnakiumModel();
		}
		paused = true;
		Log.d("GameScreen", "pause()");
	}
}
 