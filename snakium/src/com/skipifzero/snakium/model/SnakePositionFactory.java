package com.skipifzero.snakium.model;

import java.io.Serializable;


/**
 * Simple class used for reusing immutable SnakePositions.
 * 
 * @author Peter Hillerström
 * @since 2013-07-12
 * @version 2
 */
public class SnakePositionFactory implements Serializable {
	
	/**
	 * serialVersionUID == class version
	 */
	private static final long serialVersionUID = 2L;
	
	private final SnakePosition[][] positions;
	
	public SnakePositionFactory(int width, int height) {
		if(width < 1 || height < 1) {
			throw new IllegalArgumentException("width and height must be larger than 0");
		}
		this.positions = new SnakePosition[width][height];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				positions[x][y] = new SnakePosition(x, y);
			}
		}
	}
	
	public SnakePosition get(int x, int y) {
		if(x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be larger than or equal to 0");
		}
		if(x >= positions.length || y >= positions[0].length) {
			throw new IllegalArgumentException("x and y must be smaller than width or height");
		}
		return positions[x][y];
	}
	
	public int getWidthCapacity() {
		return positions.length;
	}
	
	public int getHeightCapacity() {
		return positions[0].length;
	}
	
	public boolean containsPosition(SnakePosition position) {
		return 	position != null && 
				position.getX() >= 0 && position.getY() >= 0 &&
				position.getX() < getWidthCapacity() && position.getY() < getHeightCapacity();
	}
	
	/**
	 * Returns the position adjacent to the specified position in the specified direction.
	 * Returns the same position if direction is NONE and returns a new position if position is 
	 * outside the board.
	 * @param position the SnakePosition
	 * @param direction the SnakeDirection
	 * @return
	 */
	public SnakePosition adjacent(SnakePosition position, SnakeDirection direction) {
		int x = position.getX();
		int y = position.getY();
		
		if(x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be larger than or equal to 0");
		}
		if(x >= positions.length || y >= positions[0].length) {
			throw new IllegalArgumentException("x and y must be smaller than width or height");
		}
		
		switch(direction) {
			case UP:
				if(++y >= getHeightCapacity()) {
					return new SnakePosition(x, y);
				}
				break;
			case DOWN:
				if(--y < 0) {
					return new SnakePosition(x, y);
				}
				break;
			case LEFT:
				if(--x < 0) {
					return new SnakePosition(x, y);
				}
				break;
			case RIGHT:
				if(++x >= getWidthCapacity()) {
					return new SnakePosition(x, y);
				}
				break;
			case NONE:
			default:
		}
		
		return positions[x][y];
	}
	
	/**
	 * Returns the position adjacent to the specified position in the specified direction.
	 * Returns the same position if direction is NONE and returns null if position is outside
	 * board.
	 * @param position the SnakePosition
	 * @param direction the SnakeDirection
	 * @return
	 */
	public SnakePosition adjacentNull(SnakePosition position, SnakeDirection direction) {
		int x = position.getX();
		int y = position.getY();
		
		if(x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be larger than or equal to 0");
		}
		if(x >= positions.length || y >= positions[0].length) {
			throw new IllegalArgumentException("x and y must be smaller than width or height");
		}
		
		switch(direction) {
			case UP:
				if(++y >= getHeightCapacity()) {
					return null;
				}
				break;
			case DOWN:
				if(--y < 0) {
					return null;
				}
				break;
			case LEFT:
				if(--x < 0) {
					return null;
				}
				break;
			case RIGHT:
				if(++x >= getWidthCapacity()) {
					return null;
				}
				break;
			case NONE:
			default:
		}
		
		return positions[x][y];
	}
	
	/**
	 * Returns the position adjacent to the specified position in the specified direction.
	 * Returns the same position if direction is NONE and returns position on opposite site of
	 * board if position is outside board.
	 * @param position the SnakePosition
	 * @param direction the SnakeDirection
	 * @return
	 */
	public SnakePosition adjacentWrapped(SnakePosition position, SnakeDirection direction) {
		int x = position.getX();
		int y = position.getY();
		
		if(x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be larger than or equal to 0");
		}
		if(x >= positions.length || y >= positions[0].length) {
			throw new IllegalArgumentException("x and y must be smaller than width or height");
		}
		
		switch(direction) {
			case UP:
				if(++y >= getHeightCapacity()) {
					y = 0;
				}
				break;
			case DOWN:
				if(--y < 0) {
					y = getHeightCapacity() - 1;
				}
				break;
			case LEFT:
				if(--x < 0) {
					x = getWidthCapacity() - 1;
				}
				break;
			case RIGHT:
				if(++x >= getWidthCapacity()) {
					x = 0;
				}
				break;
			case NONE:
			default:
		}
		
		return positions[x][y];
	}
}
