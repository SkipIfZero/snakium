package com.skipifzero.snakium.framework.math;

/**
 * A mutable Vector2 implementation.
 * @author Peter Hillerström
 * @since 2013-04-28
 * @version 2
 */
public final class Vector2 extends BaseVector2{

	/*
	 * Constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public Vector2(BaseVector2 vector) {
		super(vector);
	}
	
	public Vector2(double x, double y) {
		super(x, y);
	}
	
	
	/*
	 * Miscellaneous
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public boolean isMutable() {
		return true;
	}

	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets this vector to the value of the specified vector.
	 * @param vector the input vector
	 * @return this vector
	 */
	public Vector2 set(BaseVector2 vector) {
		this.x = vector.x;
		this.y = vector.y;
		return this;
	}
	
	/**
	 * Sets this vector to the value of the specified coordinates.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return this vector
	 */
	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Sets this vectors x-coordinate to the specified x-coordinate.
	 * @param x the x-coordinate
	 * @return this vector
	 */
	public Vector2 setX(double x) {
		this.x = x;
		return this;
	}
	
	/**
	 * Sets this vectors y-coordinate to the specified y-coordinate.
	 * @param y the y-coordinate
	 * @return this vector
	 */
	public Vector2 setY(double y) {
		this.y = y;
		return this;
	}
	
	
	/*
	 * Mutating methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Adds this vector with the input vector.
	 * Mutates this vector.
	 * @param vector the input vector
	 * @return this vector
	 */
	@Override
	public Vector2 add(BaseVector2 vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}

	/**
	 * Adds this vector with the input vector.
	 * Mutates this vector.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return this vector
	 */
	@Override
	public Vector2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Subtracts this vector with the input vector.
	 * Mutates this vector.
	 * @param vector the input vector
	 * @return this vector
	 */
	@Override
	public Vector2 sub(BaseVector2 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		return this;
	}

	/**
	 * Subtracts this vector with the input vector.
	 * Mutates this vector.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return this vector
	 */
	@Override
	public Vector2 sub(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * Multiplies this vector with the input factor.
	 * Mutates this vector.
	 * @param m the multiplication factor
	 * @return this vector
	 */
	@Override
	public Vector2 mult(double m) {
		this.x *= m;
		this.y *= m;
		return this;
	}

	/**
	 * Divides this vector with the input divisor.
	 * Will likely fail if d is equal to zero, use mult() to divide instead if you want to be safer.
	 * Mutates this vector.
	 * @param d the divisor
	 * @return this vector
	 */
	@Override
	public Vector2 div(double d) {
		this.x /= d;
		this.y /= d;
		return this;
	}
	
	/**
	 * Returns a unit vector based on this vector.
	 * Mutates this vector.
	 * @return this vector
	 */
	@Override
	public Vector2 makeUnit() {
		double length = this.getLength();
		if(length != 0){ //Checks if length is 0 to avoid division by 0.
			this.x /= length;
			this.y /= length;
		}
		return this;
	}

	/**
	 * Returns a unit vector with the requested angle.
	 * Mutates this vector.
	 * @param degAngle the angle in degrees
	 * @return this vector
	 */
	@Override
	public Vector2 makeUnit(double degAngle) {
		double radAngle = degAngle * DEG_TO_RAD;
		this.x = Math.cos(radAngle);
		this.y = Math.sin(radAngle);		
		return this;
	}

	/**
	 * Rotates this vector around its origin by the requested angle.
	 * Mutates this vector.
	 * @param degAngle the angle in degrees
	 * @return this vector
	 */
	@Override
	public Vector2 rotate(double degAngle) {
		double radAngle = degAngle * DEG_TO_RAD;
		double cos = Math.cos(radAngle);
		double sin = Math.sin(radAngle);
		
		double tempX = this.x * cos - this.y * sin;
		double tempY = this.x * sin + this.y * cos;
		this.x = tempX;
		this.y = tempY;
		
		return this;
	}

	
	/*
	 * Others
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public Vector2 clone() {
		return new Vector2(this);
	}
}
