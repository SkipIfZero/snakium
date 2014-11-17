package com.skipifzero.snakium.model;

/**
 * Enum used for input and direction in SnakeModel's.
 * 
 * @author Peter Hillerström
 * @version 1
 */
public enum SnakeDirection {
	NONE, UP, DOWN, LEFT, RIGHT;
	
	/**
	 * Returns the opposite direction of this direction.
	 * UP -> DOWN, DOWN -> UP
	 * LEFT -> RIGHT, RIGHT -> LEFT
	 * NONE is a special case and returns NONE.
	 * @return the opposite direction of this direction.
	 */
	public SnakeDirection invertDirection() {
		switch (this) {
			case UP:
				return SnakeDirection.DOWN;
			case DOWN:
				return SnakeDirection.UP;
			case LEFT:
				return SnakeDirection.RIGHT;
			case RIGHT:
				return SnakeDirection.LEFT;
			case NONE:
				return SnakeDirection.NONE;
			default:
				throw new AssertionError();
		}
	}
	
	/**
	 * Checks whether two SnakeDirection's are opposite from each other.
	 * In the special case that both SnakeDirection's are NONE this returns true.
	 * @param direction1 the first SnakeDirection
	 * @param direction2 the second SnakeDirection
	 * @return whether the specified SnakeDirection's are opposite from each other
	 */
	public static boolean isOpposite(SnakeDirection direction1, SnakeDirection direction2) {
		return direction2 == direction1.invertDirection();
	}
	
	/**
	 * Checks whether it is a right turn from the first SnakeDirection to the second.
	 * For example isRightTurn(DOWN, RIGHT) returns true, as do isRightTurn(RIGHT, UP).
	 * If any of the specified SnakeDirection's are NONE this returns false.
	 * @param from the first SnakeDirection
	 * @param to the second SnakeDirection
	 * @return whether it is a right turn from the first SnakeDirection to the second 
	 */
	public static boolean isRightTurn(SnakeDirection from, SnakeDirection to) {
		switch (from) {
			case UP:
				return to == SnakeDirection.LEFT;
			case DOWN:
				return to == SnakeDirection.RIGHT;
			case LEFT:
				return to == SnakeDirection.DOWN;
			case RIGHT:
				return to == SnakeDirection.UP;
			case NONE:
				return false;
			default:
				throw new AssertionError();
		}
	}
	
	/**
	 * Checks whether it is a left turn from the first SnakeDirection to the second.
	 * For example isLeftTurn(DOWN, LEFT) returns true, as do isLeftTurn(RIGHT, DOWN).
	 * If any of the specified SnakeDirection's are NONE this returns false.
	 * @param from the first SnakeDirection
	 * @param to the second SnakeDirection
	 * @return whether it is a right turn from the first SnakeDirection to the second 
	 */
	public static boolean isLeftTurn(SnakeDirection from, SnakeDirection to) {
		switch (from) {
			case UP:
				return to == SnakeDirection.RIGHT;
			case DOWN:
				return to == SnakeDirection.LEFT;
			case LEFT:
				return to == SnakeDirection.UP;
			case RIGHT:
				return to == SnakeDirection.DOWN;
			case NONE:
				return false;
			default:
				throw new AssertionError();
		}
	}
}
