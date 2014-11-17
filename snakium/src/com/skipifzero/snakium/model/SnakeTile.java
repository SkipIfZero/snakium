package com.skipifzero.snakium.model;

import java.io.Serializable;


/**
 * Mutable Class used to describe a tile on the gameboard of a Snake game.
 * 
 * @author Peter Hillerström
 * @since 2013-07-12
 * @version 2
 */
public class SnakeTile implements Serializable {
	
	/**
	 * serialVersionUID == class version
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * Enum used to describe the type of a tile on the gameboard of a Snake game. 
	 * @author Peter Hillerström
	 */
	public enum Type {
		//Misc
		EMPTY, OBJECT, BONUS_OBJECT,
		
		//Snake
		SNAKE_HEAD, SNAKE_BODY, SNAKE_TAIL;
	}
	
	private Type type;
	private SnakeDirection from, to;
	private boolean digesting;
	private SnakePosition position;
	
	/*
	 * Constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public SnakeTile(Type type, SnakeDirection from, SnakeDirection to, boolean digesting, SnakePosition position) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.digesting = digesting;
		this.position = position;
	}
	
	public SnakeTile(SnakeTile tile) {
		this(tile.getType(), tile.getFromDirection(), tile.getToDirection(), tile.isDigesting(), tile.getPosition());
	}
	
	public SnakeTile() {
		this(Type.EMPTY, SnakeDirection.NONE, SnakeDirection.NONE, false, null);
	}
	
	/*
	 * Public Methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Two SnakeTile's are considered overlapping if their SnakePosition's are equal.
	 * @param tile the SnakeTile to compare with
	 * @return whether the two SnakeTile's are overlapping
	 */
	public boolean overlap(SnakeTile tile) {
		if(this.position == null) {
			return false;
		}
		return this.position.equals(tile.position);
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public Type getType() {
		return type;
	}
	
	public SnakeDirection getFromDirection() {
		return from;
	}
	
	public SnakeDirection getToDirection() {
		return to;
	}
	
	public boolean isDigesting() {
		return digesting;
	}
	
	public SnakePosition getPosition() {
		return position;
	}
	
	public int getX() {
		return position.getX();
	}
	
	public int getY() {
		return position.getY();
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void setFromDirection(SnakeDirection from) {
		this.from = from;
	}
	
	public void setToDirection(SnakeDirection to) {
		this.to = to;
	}
	
	public void setDigesting(boolean digesting) {
		this.digesting = digesting;
	}
	
	public void setPosition(SnakePosition position) {
		this.position = position;
	}
}
