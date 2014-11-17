package com.skipifzero.snakium.viewmodels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.BitmapFontRenderer;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.audio.SoundEffect;
import com.skipifzero.snakium.framework.entities.FloatingTextEntity;
import com.skipifzero.snakium.framework.input.Pool;
import com.skipifzero.snakium.framework.input.Pool.PoolObjectFactory;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.math.Vector2;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.framework.opengl.SpriteBatcher;
import com.skipifzero.snakium.framework.opengl.TextureRegion;
import com.skipifzero.snakium.model.SnakeDirection;
import com.skipifzero.snakium.model.SnakePosition;
import com.skipifzero.snakium.model.SnakePositionFactory;
import com.skipifzero.snakium.model.SnakeTile;
import com.skipifzero.snakium.model.SnakeTile.Type;
import com.skipifzero.snakium.model.SnakiumModel;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;
import com.skipifzero.snakium.model.SnakiumStats;

public class SnakiumModelDescriptor {
	
	private static enum SoundEffectType {
		OBJECT_AQUIRED, BONUS_OBJECT_AQUIRED, BONUS_STARTED, BONUS_FAILED, SNAKE_TURN, WALL_CROSS, GAME_OVER;
	}
	
	private final SnakiumModel model;
	private final SnakiumConfig config;
	private final SnakiumStats stats;
	private final BoundingRectangle bounds;
	private final double borderWidth, tileSize, offsetX, offsetY;
	private final List<FloatingTextEntity> floatingTexts = new ArrayList<FloatingTextEntity>(30);
	private final Pool<FloatingTextEntity> floatingTextPool;
	private final List<SoundEffectType> soundEffectsToPlay = new ArrayList<SoundEffectType>(10);
	
	//Hack variables
	private int currentHeadPositionX, currentHeadPositionY, lastHeadPositionX, lastHeadPositionY;
	
	//State variables for sound effects
	private boolean sndBonus, sndGameOver;
	private int sndMissedBonus, sndTurns, sndWraps;
	
	//Temp variables
	private final Vector2 tempVec1 = new Vector2(0,0);
	private final Vector2 tempVec2 = new Vector2(0,0);
	
	public SnakiumModelDescriptor(double x, double y, double maxWidth, double maxHeight, double borderWidthFactor, SnakiumModel model) {
		this.model = model;
		this.config = model.getConfig();
		this.stats = model.getStats();
		this.borderWidth = (maxWidth > maxHeight ? maxWidth : maxHeight) * borderWidthFactor;
		
		//Calculates boardWidth and boardHeight based on model and max size.
		double tileSize = (maxWidth - 2*borderWidth) / model.getBoardWidth();
		if((tileSize*model.getBoardHeight() + 2*borderWidth) > (maxHeight)) {
			tileSize = (maxHeight - 2*borderWidth) / model.getBoardHeight();
		}
		this.tileSize = tileSize;
		double boundsWidth = tileSize*model.getBoardWidth() + 2*borderWidth;
		double boundsHeight = tileSize*model.getBoardHeight() + 2*borderWidth;
		this.bounds = new BoundingRectangle(x, y + maxHeight/2 - boundsHeight/2, boundsWidth, boundsHeight);
		this.offsetX = bounds.getX() - (tileSize*model.getBoardWidth())/2 + tileSize/2;
		this.offsetY = bounds.getY() - (tileSize*model.getBoardHeight())/2 + tileSize/2;
		
		//Creates FloatingText pool
		PoolObjectFactory<FloatingTextEntity> factory = new PoolObjectFactory<FloatingTextEntity>() {
			@Override
			public FloatingTextEntity createObject() {
				return new FloatingTextEntity();
			}
		};
		this.floatingTextPool = new Pool<FloatingTextEntity>(factory, 40);
		
		initializeHackVariables();
		
		//State variables for sound effects
		sndBonus = model.bonusTimeLeft() > 0;
		sndGameOver = model.isGameOver();
		sndMissedBonus = stats.missedBonusObjects();
		sndTurns = stats.leftTurns() + stats.rightTurns();
		sndWraps = stats.wallsCrossed();
	}
	
	public void update(double deltaTime) {
		int currentTileScore = model.getCurrentTileScore();
		updateFloatingTexts(deltaTime, currentTileScore);
		updateSoundEffectsToPlay(currentTileScore);
	}
	
	public void render(Assets assets) {
		SpriteBatcher b = assets.batcher;
		
		b.beginBatch(assets.texAtlas128);
		
		SnakiumUtils.renderBorder(bounds, borderWidth, assets);
		
		if(Settings.showBoard()) {
			renderInternalBorder(assets);
		}
		
		renderSnakeTiles(assets);
		
		b.renderBatch();
		
		if(Settings.floatingTexts()) {
			renderFloatingTexts(assets.font);
		}
		
		if(Settings.sound()) {
			playSoundEffects(assets);
		}
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public double getX() {
		return bounds.getX();
	}
	
	public double getY() {
		return bounds.getY();
	}
	
	public double getWidth() {
		return bounds.getWidth();
	}
	
	public double getHeight() {
		return bounds.getHeight();
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private void renderFloatingTexts(BitmapFontRenderer font) {
		font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		font.setVerticalAlignment(VerticalAlignment.CENTER);
		for(FloatingTextEntity floatingText : floatingTexts) {
			font.completeDraw(floatingText.getPosition(), floatingText.getSize(), floatingText.getColor(), floatingText.getText());
		}
	}
	
	private void renderSnakeTiles(Assets assets) {
		double angle, widthSign;
		for(SnakeTile tile : model.getSnakeTiles()) {
			
			//Calculate angle
			switch(tile.getFromDirection()) {
				case UP:
					angle = 270 -90;
					break;
				case NONE:
				case DOWN:
					angle = 90 -90;
					break;
				case LEFT:
					angle = 0 -90;
					break;
				case RIGHT:
					angle = 180 -90;
					break;
				default:
					throw new AssertionError();
			}
			
			//Check if left turn.
			if(SnakeDirection.isLeftTurn(tile.getFromDirection(), tile.getToDirection())) {
				widthSign = -1;
			} else {
				widthSign = 1;
			}
			
			assets.batcher.draw(offsetX + tile.getX()*tileSize, offsetY + tile.getY()*tileSize, widthSign*tileSize, tileSize, angle, getSnakeTileRegion(tile, assets));
		}
	}
	
	private void renderInternalBorder(Assets assets) {
		for(int x = 0; x < model.getBoardWidth(); x++) {
			for(int y = 0; y < model.getBoardHeight(); y++) {
				assets.batcher.draw(offsetX + x*tileSize, offsetY + y*tileSize, tileSize, tileSize, assets.TILE_BORDER);
			}
		}
	}
	
	private void updateSoundEffectsToPlay(int currentTileScore) {
		soundEffectsToPlay.clear();
		
		if(currentTileScore == config.pointsPerObject) {
			soundEffectsToPlay.add(SoundEffectType.OBJECT_AQUIRED);
		} else if(currentTileScore == config.pointsPerBonusObject) {
			soundEffectsToPlay.add(SoundEffectType.BONUS_OBJECT_AQUIRED);
		}
		
		boolean currBonus = model.bonusTimeLeft() > 0;
		if(currBonus && !sndBonus) {
			soundEffectsToPlay.add(SoundEffectType.BONUS_STARTED);
			//Minor hack
			Iterator<SoundEffectType> itr = soundEffectsToPlay.iterator();
			while(itr.hasNext()) {
				if(itr.next() == SoundEffectType.OBJECT_AQUIRED) {
					itr.remove();
				}
			}
		}
		if(stats.missedBonusObjects() > sndMissedBonus) {
			soundEffectsToPlay.add(SoundEffectType.BONUS_FAILED);
		}
		sndMissedBonus = stats.missedBonusObjects();
		sndBonus = currBonus;
		
		int currTurns = stats.leftTurns() + stats.rightTurns();
		if(currTurns > sndTurns) {
			soundEffectsToPlay.add(SoundEffectType.SNAKE_TURN);
		}
		sndTurns = currTurns;
		
		int currWraps = stats.wallsCrossed();
		if(currWraps > sndWraps) {
			soundEffectsToPlay.add(SoundEffectType.WALL_CROSS);
		}
		sndWraps = currWraps;
		
		if(model.isGameOver() && !sndGameOver) {
			soundEffectsToPlay.add(SoundEffectType.GAME_OVER);
			sndGameOver = true;
		}
	}
	
	private void playSoundEffects(Assets assets) {
		SoundEffect sndEffect = null;
		for(SoundEffectType type : soundEffectsToPlay) {
			sndEffect = null;
			switch(type) {
				case OBJECT_AQUIRED:
					sndEffect = assets.OBJECT_AQUIRED_SND;
					break;
				case BONUS_OBJECT_AQUIRED:
					sndEffect =	assets.BONUS_OBJECT_AQUIRED_SND;
					break;
				case BONUS_STARTED:
					sndEffect =	assets.BONUS_STARTED_SND;
					break;
				case BONUS_FAILED:
					sndEffect =	assets.BONUS_FAILED_SND;
					break;
				case SNAKE_TURN:
					sndEffect =	assets.SNAKE_TURN_SND;
					break;
				case WALL_CROSS:
					if(assets.WALL_CROSS_SND != null) {
						assets.WALL_CROSS_SND.play();
					}
					break;
				case GAME_OVER:
					sndEffect =	assets.GAME_OVER_SND;
					break;
				default:
					throw new AssertionError();
			}
			if(sndEffect != null) {
				sndEffect.play();
			}
		}
	}
	
	private void updateFloatingTexts(double deltaTime, int currentTileScore) {
		//Updates current floating texts
		Iterator<FloatingTextEntity> iterator = floatingTexts.iterator();
		while(iterator.hasNext()) {
			FloatingTextEntity text = iterator.next();
			text.update(deltaTime);
			if(!text.isActive()) {
				floatingTextPool.recycleObject(text);
				iterator.remove();
			}
		}
		
		//Check if new FloatingText needs to be added and does so if that is the case.
		if(currentTileScore > 0) {
			for(SnakeTile tile : model.getSnakeTiles()) {
				if(tile.getType() == Type.SNAKE_HEAD) {
					SnakePosition snakePos = tile.getPosition();
					tempVec1.set(offsetX + snakePos.getX()*tileSize, offsetY + snakePos.getY()*tileSize);
				}
			}
			
			tempVec2.set(0, tileSize/2);
			FloatingTextEntity floatingText = floatingTextPool.getRecycledObject();
			floatingText.set(String.valueOf(currentTileScore), tempVec1, tempVec2, Const.FONT_COMPLETE_COLOR, tileSize*0.5, 180, 1);
			floatingTexts.add(floatingText);
		}
	}
	
	private void initializeHackVariables() {
		List<SnakeTile> tiles = model.getSnakeTiles();
		for(SnakeTile tile : tiles) {
			if(tile.getType() == Type.SNAKE_HEAD) {
				currentHeadPositionX = tile.getX();
				currentHeadPositionY = tile.getY();
				SnakePositionFactory posFact = new SnakePositionFactory(model.getBoardWidth(), model.getBoardHeight()); //TODO: LOTS OF GARBAGE. ARGAJE
				SnakePosition pos = posFact.adjacentWrapped(tile.getPosition(), tile.getFromDirection());
				lastHeadPositionX = pos.getX();
				lastHeadPositionY = pos.getY();
				break;
			}
		}
	}
	
	@SuppressWarnings("all")
	private TextureRegion getSnakeTileRegion(SnakeTile tile, Assets assets) {
		boolean isTurn = !SnakeDirection.isOpposite(tile.getFromDirection(), tile.getToDirection());
		
		switch(tile.getType()) {
		case SNAKE_HEAD:
			//Hack used to check if body tile previously was a head tile.
			if(!tile.getPosition().equals(currentHeadPositionX, currentHeadPositionY)) {
				lastHeadPositionX = currentHeadPositionX;
				lastHeadPositionY = currentHeadPositionY;
				currentHeadPositionX = tile.getPosition().getX();
				currentHeadPositionY = tile.getPosition().getY();
			}
			if(model.getProgress() <= 0.50) { //Frame1
				if(isTurn) {
					return assets.HEAD_D2U_F1;
				} else {
					return assets.HEAD_D2U_F1;
				}
			} else { //Frame 2
				return assets.HEAD_D2U_F2;
			}
		case SNAKE_BODY:
			//Check if this was previous head.
			if(model.getProgress() <= 0.50 && tile.getPosition().equals(lastHeadPositionX, lastHeadPositionY)) {
				if(isTurn) {
					if(tile.isDigesting()) {
						if(model.isGameOver()) {
							return assets.DEADHEAD_D2R_DIG_F3;
						} else {
							return assets.HEAD_D2R_DIG_F3;
						}
					} else {
						if(model.isGameOver()) {
							return assets.DEADHEAD_D2R_F3;
						} else  {
							return assets.HEAD_D2R_F3;
						}
					}
				} else {
					if(tile.isDigesting()) {
						if(model.isGameOver()) {
							return assets.DEADHEAD_D2U_DIG_F3;
						} else {
							return assets.HEAD_D2U_DIG_F3;
						}
					} else {
						if(model.isGameOver()) {
							return assets.DEADHEAD_D2U_F3;
						} else {
							return assets.HEAD_D2U_F3;
						}
					}
				}
			}
			if(tile.isDigesting()) {
				if(isTurn) {
					return assets.BODY_D2R_DIG;
				} else {
					return assets.BODY_D2U_DIG;
				}
			} else {
				if(isTurn) {
					return assets.BODY_D2R;
				} else {
					return assets.BODY_D2U;
				}
			}
		case SNAKE_TAIL:
			if(model.getProgress() <= 0.50) { //Frame1
				if(tile.isDigesting()) {
					if(isTurn) {
						return assets.TAIL_D2R_DIG_F1;
					} else {
						return assets.TAIL_D2U_DIG_F1;
					}
				} else {
					if(isTurn) {
						return assets.TAIL_D2R_F1;
					} else {
						return assets.TAIL_D2U_F1;
					}
				}
			} else { //Frame 2
				if(tile.isDigesting()) {
					if(isTurn) {
						return assets.TAIL_D2R_DIG_F2;
					} else {
						return assets.TAIL_D2U_DIG_F2;
					}
				} else {
					if(isTurn) {
						return assets.TAIL_D2R_F2;
					} else {
						return assets.TAIL_D2U_F2;
					}
				}
			}
		case OBJECT:
			return assets.OBJECT;
		case BONUS_OBJECT:
			return assets.BONUS_OBJECT;
		case EMPTY:
			return null;
		default:
			throw new AssertionError();
		}
	}
}
