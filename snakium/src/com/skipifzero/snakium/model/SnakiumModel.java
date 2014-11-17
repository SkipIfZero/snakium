package com.skipifzero.snakium.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.skipifzero.snakium.model.SnakeTile.Type;

/**
 * Snakium's implementation SnakeModel implementation.
 * 
 * @author Peter Hillerström
 * @since 2013-08-15
 * @version 2
 */
public final class SnakiumModel implements SnakeModel, Serializable {
	
	/**
	 * serialVersionUID == class version
	 */
	private static final long serialVersionUID = 2L;

	public final static class SnakiumConfig implements Serializable {
		private static final long serialVersionUID = 7830300255063863400L;
		
		public final int id;
		public final boolean hasWrapping;
		public final int boardWidth;
		public final int boardHeight;
		public final double tilesPerSecond;
		public final boolean hasSpeedIncrease;
		public final double speedIncreasePerObject;
		public final int pointsPerObject;
		public final boolean hasBonus;
		public final int bonusFrequency;
		public final int bonusDuration;
		public final int pointsPerBonusObject;
		
		public SnakiumConfig(	int id,
								boolean hasWrapping,
								int boardWidth,
								int boardHeight,
								double tilesPerSecond,
								boolean hasSpeedIncrease,
								double speedIncreasePerObject,
								int pointsPerObject,
								boolean hasBonus,
								int bonusFrequency,
								int bonusDuration,
								int pointsPerBonusObject) {
			this.id = id;
			this.hasWrapping = hasWrapping;
			this.boardWidth = boardWidth;
			this.boardHeight = boardHeight;
			this.tilesPerSecond = tilesPerSecond;
			this.hasSpeedIncrease = hasSpeedIncrease;
			this.speedIncreasePerObject = speedIncreasePerObject;
			this.pointsPerObject = pointsPerObject;
			this.hasBonus = hasBonus;
			this.bonusFrequency = bonusFrequency;
			this.bonusDuration = bonusDuration;
			this.pointsPerBonusObject = pointsPerBonusObject;
		}
	}
	
	//Tools
	private final SnakePositionFactory posFactory;
	private final List<SnakePosition> freePositions;
	
	private final SnakiumConfig cfg;
	private final SnakiumStats stats;
	
	private final List<SnakeTile> snakeTilesToDisplay;
	private final Deque<SnakeTile> snake;
	private final List<SnakeTile> objects;
	
	//State variables
	private SnakeDirection direction;
	private double tilesPerSecond;
	private double progress;
	private int score;
	private int currentTileScore;
	private boolean gameOver;
	private boolean bonusTime;
	private int bonusTimeLeft;
	private int objectsSinceBonus;
	
	public SnakiumModel(SnakiumConfig cfg) {
		//Tools
		this.posFactory = new SnakePositionFactory(cfg.boardWidth, cfg.boardHeight);
		
		//Creates a list with all the currently free positions on the board.
		this.freePositions = new ArrayList<SnakePosition>(cfg.boardWidth*cfg.boardHeight);
		for(int x = 0; x < posFactory.getWidthCapacity(); x++) {
			for(int y = 0; y < posFactory.getHeightCapacity(); y++) {
				this.freePositions.add(posFactory.get(x, y));
			}
		}
		
		this.cfg = cfg;
		this.stats = new SnakiumStats(3, cfg.tilesPerSecond);
		
		this.snakeTilesToDisplay = new ArrayList<SnakeTile>(cfg.boardWidth*cfg.boardHeight);
		this.snake = new LinkedList<SnakeTile>();
		this.objects = new LinkedList<SnakeTile>();
		objects.add(new SnakeTile(Type.OBJECT, SnakeDirection.NONE, SnakeDirection.NONE, false, null));
		objects.add(new SnakeTile(Type.BONUS_OBJECT, SnakeDirection.NONE, SnakeDirection.NONE, false, null));
		
		this.direction = SnakeDirection.UP;
		this.tilesPerSecond = cfg.tilesPerSecond;
		this.progress = 0.0;
		this.score = 0;
		this.currentTileScore = -1;
		this.gameOver = false;
		this.bonusTime = false;
		this.bonusTimeLeft = -1;
		this.objectsSinceBonus = 0;
		
		//Creates initial Snake and removes positions from freePositions list.
		snake.addFirst(new SnakeTile(SnakeTile.Type.SNAKE_HEAD, SnakeDirection.DOWN, SnakeDirection.UP, false, posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2)));
		snake.addLast(new SnakeTile(SnakeTile.Type.SNAKE_BODY, SnakeDirection.DOWN, SnakeDirection.UP, false,  posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2 - 1)));
		snake.addLast(new SnakeTile(SnakeTile.Type.SNAKE_TAIL, SnakeDirection.DOWN, SnakeDirection.UP, false,  posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2 - 2)));
		freePositions.remove(posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2));
		freePositions.remove(posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2 - 1));
		freePositions.remove(posFactory.get(cfg.boardWidth/2, cfg.boardHeight/2 - 2));
		
		placeNormalObjects();
		updateSnakeTilesToDisplay();
	}
	
	@Override
	public void update(double deltaTime, SnakeDirection inputDirection) {
		//Check if game is over, returns if it is.
		if(gameOver) {
			return;
		}
		
		//Checks to make sure inputDirection is valid and modifies direction accordingly.
		if(inputDirection != SnakeDirection.NONE && inputDirection != snake.getFirst().getFromDirection()) {
			direction = inputDirection;
		}
		
		//Updates direction head is facing.
		snake.getFirst().setToDirection(direction);
		
		//Updates progress, returns if it is not yet time to move snake.
		progress += deltaTime*tilesPerSecond;
		if(progress < 1) {
			return;
		}
		progress -= 1;
		
		advance();
	}
	
	public void advanceFrame1(SnakeDirection direction) {
		this.direction = direction;
		snake.getFirst().setToDirection(direction);
		progress = 0;
		advance();
	}
	
	public void advanceFrame2() {
		progress = 0.75;
	}
	
	public void advance() {
		stats.amountOfMoves++;
		
		if(SnakeDirection.isLeftTurn(snake.getFirst().getFromDirection(), direction)) {
			stats.leftTurns++;
		} else if(SnakeDirection.isRightTurn(snake.getFirst().getFromDirection(), direction)) {
			stats.rightTurns++;
		}
		updateSnake();
		
		gameOverCheck();
		
		objectsOverlapCheck();
		
		if(tilesPerSecond > stats.maxSpeedAchieved) {
			stats.maxSpeedAchieved = tilesPerSecond;
		}
		
		updateSnakeTilesToDisplay();
	}

	@Override
	public List<SnakeTile> getSnakeTiles() {
		return snakeTilesToDisplay;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public int getBoardWidth() {
		return cfg.boardWidth;
	}

	@Override
	public int getBoardHeight() {
		return cfg.boardHeight;
	}

	@Override
	public int bonusTimeLeft() {
		return bonusTimeLeft;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getCurrentTileScore() {
		int temp = currentTileScore;
		currentTileScore = -1;
		return temp;
	}

	@Override
	public boolean isGameOver() {
		return gameOver;
	}
	
	public SnakiumStats getStats() {
		return stats;
	}
	
	public SnakiumConfig getConfig() {
		return cfg;
	}
	
	/*
	 * Private methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private void placeOnRandomFreePosition(SnakeTile tile) {
		if(freePositions.size() <= 0) {
			tile.setPosition(null);
			return;
		}
		
		Collections.shuffle(freePositions);
		tile.setPosition(freePositions.remove(0));
	}
	
	private void placeNormalObjects() {
		for(SnakeTile tile : objects) {
			if(tile.getType() == Type.OBJECT) {
				placeOnRandomFreePosition(tile);
			}
		}
	}
	
	private void placeBonusObjects() {
		for(SnakeTile tile : objects) {
			if(tile.getType() == Type.BONUS_OBJECT) {
				placeOnRandomFreePosition(tile);
			}
		}
	}
	
	private void removeBonusObjects() {
		for(SnakeTile tile : objects) {
			if(tile.getType() == Type.BONUS_OBJECT) {
				if(tile.getPosition() != null) {
					freePositions.add(tile.getPosition());
				}
				tile.setPosition(null);
			}
		}
	}
	
	private boolean hasActiveBonusObjects() {
		for(SnakeTile tile : objects) {
			if(tile.getType() == Type.BONUS_OBJECT) {
				if(tile.getPosition() != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void activateBonus() {
		bonusTime = true;
		bonusTimeLeft = cfg.bonusDuration;
		placeBonusObjects();
	}
	
	private void deactivateBonus() {
		bonusTime = false;
		bonusTimeLeft = -1;
		objectsSinceBonus = 0;
		removeBonusObjects();
	}
	
	private int objectValue(SnakeTile object) {
		switch(object.getType()) {
			case OBJECT:
				return cfg.pointsPerObject;
			case BONUS_OBJECT:
				return cfg.pointsPerBonusObject;
			case EMPTY:
			case SNAKE_BODY:
			case SNAKE_HEAD:
			case SNAKE_TAIL:
			default:
				throw new IllegalStateException();
		}
	}
	
	private void updateScore(SnakeTile object) {
		currentTileScore = objectValue(object);
		score += currentTileScore;
		stats.score = score;
	}
	
	private void updateSnake() {
		//Gets next head position.
		snake.getFirst().setType(Type.SNAKE_BODY);
		SnakePosition newHeadPosition;
		if(cfg.hasWrapping) {
			newHeadPosition = posFactory.adjacentWrapped(snake.getFirst().getPosition(), direction);
			if(posFactory.adjacentNull(snake.getFirst().getPosition(), direction) == null) { //Small hack to check if snake crossed wall for stats.
				stats.wallsCrossed++;
			}
		} else {
			newHeadPosition = posFactory.adjacent(snake.getFirst().getPosition(), direction);
		}
		freePositions.remove(newHeadPosition);
		
		//Moves snake to new position.
		SnakeTile newHead;
		if(snake.getLast().isDigesting()) {
			newHead = new SnakeTile();
			snake.getLast().setDigesting(false);
			stats.snakeLength++;
		} else {
			newHead = snake.removeLast();
			if(newHead.getPosition() != newHeadPosition) { //Adds position of last tail to list of free positions if it is not the new head position.
				freePositions.add(newHead.getPosition());
			}
			snake.getLast().setType(Type.SNAKE_TAIL);
		}
		newHead.setType(Type.SNAKE_HEAD);
		newHead.setPosition(newHeadPosition);
		newHead.setFromDirection(direction.invertDirection());
		newHead.setToDirection(direction);
		snake.addFirst(newHead);
	}
	
	private void gameOverCheck() {
		SnakeTile head = snake.getFirst();
		if(!posFactory.containsPosition(head.getPosition())) {
			gameOver = true;
			return;
		}
		for(SnakeTile tile : snake) {
			if(tile != head && head.overlap(tile)) {
				gameOver = true;
				return;
			}
		}
	}
	
	private void objectsOverlapCheck() {
		SnakeTile head = snake.getFirst();
		for(SnakeTile object : objects) {
			if(head.overlap(object) && object.getType() == Type.BONUS_OBJECT) {
				head.setDigesting(true);
				object.setPosition(null);
				if(cfg.hasSpeedIncrease) {
					tilesPerSecond += cfg.speedIncreasePerObject;
				}
				if(!hasActiveBonusObjects()) {
					deactivateBonus();
				}
				updateScore(object);
				stats.bonusObjectsConsumed++;
			}
		}
		
		//Checks if it is time to deactivate bonus if it is enabled.
		if(cfg.hasBonus && bonusTime) {
			if(--bonusTimeLeft <= 0) {
				deactivateBonus();
				stats.missedBonusObjects++;
			}
		}
		
		for(SnakeTile object : objects) {
			if(head.overlap(object) && object.getType() == Type.OBJECT) {
				head.setDigesting(true);
				placeOnRandomFreePosition(object);
				if(cfg.hasSpeedIncrease) {
					tilesPerSecond += cfg.speedIncreasePerObject;
				}
				if(cfg.hasBonus) {
					if(++objectsSinceBonus == cfg.bonusFrequency) {
						activateBonus();
					}
				}
				updateScore(object);
				stats.objectsConsumed++;
			}
		}
	}
	
	private void updateSnakeTilesToDisplay() {
		snakeTilesToDisplay.clear();
		snakeTilesToDisplay.addAll(snake);
		for(SnakeTile object : objects) {
			if(object.getPosition() != null) {
				snakeTilesToDisplay.add(object);
			}
		}
	}
}
