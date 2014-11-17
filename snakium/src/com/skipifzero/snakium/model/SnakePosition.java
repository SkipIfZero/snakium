package com.skipifzero.snakium.model;

import java.io.Serializable;

/**
 * Immutable position on the gameboard.
 * 
 * A SnakePosition is defined as two integers as follows:
 * 0 <= x < gameboard.width
 * 0 <= y < gameboard.height
 * Where (0,0) is the bottom left corner tile and (width - 1, height - 1) is the upper right corner
 * tile.
 * 
 * @author Peter Hillerström
 * @since 2013-07-12
 * @version 3
 */
public final class SnakePosition implements Serializable {

	/**
	 * serialVersionUID == class version
	 */
	private static final long serialVersionUID = 3L;
	
	private final int x, y;
	
	/**
	 * Creates a new SnakePosition with the specified position.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public SnakePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new SnakePosition with the specified position.
	 * @param position the position
	 */
	public SnakePosition(SnakePosition position) {
		this(position.x, position.y);
	}
	
	/**
	 * Returns the x-coordinate of this SnakePosition on the gameboard.
	 * 0 <= x < gameboard.width
	 * @return x-coordinate of this SnakePosition
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the y-coordinate of this SnakePosition on the gameboard.
	 * 0 <= y < gameboard.height
	 * @return y-coordinate of this SnakePosition on the gameboard.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Returns true if this.x == x and this.y == y.
	 * @param x
	 * @param y
	 * @return this.x == x && this.y == y
	 */
	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SnakePosition other = (SnakePosition) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
}
