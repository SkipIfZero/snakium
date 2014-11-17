package com.skipifzero.snakium.model;

import java.io.Serializable;

public final class SnakiumStats implements Serializable {
	private static final long serialVersionUID = 1902359886977996507L;
	
	protected int score = 0;
	protected int snakeLength = 0;
	protected int objectsConsumed = 0;
	protected int bonusObjectsConsumed = 0;
	protected int missedBonusObjects = 0;
	protected int amountOfMoves = 0;
	protected int leftTurns = 0;
	protected int rightTurns = 0;
	protected int wallsCrossed = 0;
	protected double maxSpeedAchieved = 0;
	
	protected SnakiumStats(int snakeStartLength, double startSpeed) {
		this.snakeLength = snakeStartLength;
		this.maxSpeedAchieved = startSpeed;
	}
	
	public int score() {
		return score;
	}
	
	public int snakeLength() {
		return snakeLength;
	}
	
	public int objectsConsumed() {
		return objectsConsumed;
	}
	
	public int bonusObjectsConsumed() {
		return bonusObjectsConsumed;
	}
	
	public int missedBonusObjects(){
		return missedBonusObjects;
	}
	
	public int amountOfMoves() {
		return amountOfMoves;
	}
	
	public int leftTurns() {
		return leftTurns;
	}
	
	public int rightTurns() {
		return rightTurns;
	}
	
	public int wallsCrossed() {
		return wallsCrossed;
	}
	
	public double maxSpeedAchieved() {
		return maxSpeedAchieved;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Score: ").append(score).append("\n")
		.append("Snake length: ").append(snakeLength).append("\n")
		.append("Objects consumed: ").append(objectsConsumed).append("\n")
		.append("Bonus objects consumed: ").append(bonusObjectsConsumed).append("\n")
		.append("Missed bonus objects: ").append(missedBonusObjects).append("\n")
		.append("Amount of moves: ").append(amountOfMoves).append("\n")
		.append("Amount of left turns: ").append(leftTurns).append("\n")
		.append("Amount of right turns: ").append(rightTurns).append("\n")
		.append("Walls crossed: ").append(wallsCrossed).append("\n")
		.append("Max speed achieved: ").append(maxSpeedAchieved);
		return b.toString();
	}
}
