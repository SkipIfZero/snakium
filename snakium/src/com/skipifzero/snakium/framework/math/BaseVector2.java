package com.skipifzero.snakium.framework.math;

/**
 * An abstract class used for 2 dimensional vector implementations (Vector2 and its immutable counterpart FinalVector2).
 * 
 * This class's internal variables are protected, so it's important this class and its implementations are placed alone 
 * in its own package. Otherwise there is a risk someone directly accesses the instance variables in FinalVector2.
 * 
 * Inspired by similar class found in Beginning Android Games by Mario Zechner.
 * 
 * @author Peter Hillerström
 * @since 2013-04-28
 * @version 2
 */
public abstract class BaseVector2 implements Cloneable, Comparable<BaseVector2> {
	public static final double DEG_TO_RAD = Math.PI / 180;
	public static final double RAD_TO_DEG = 180 / Math.PI;
	
	/**
	 * Protected so they can be directly accessed by subclasses, but not public so immutable implementations
	 * is still possible.
	 */
	protected double x, y;
	
	
	/*
	 * Constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public BaseVector2(BaseVector2 vector) {
		this.x = vector.getX();
		this.y = vector.getY();
	}
	
	public BaseVector2(double x, double y) {
		this.x = x;
		this.y = y;
	} 
	
	
	/*
	 * Miscellaneous
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns the x coordinate of this vector.
	 * @return x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of this vector.
	 * @return y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Checks if this vector implementation is mutable or not.
	 * If it is mutable, it is obliged to mutate and return this vector in methods where it is appropriate.
	 * Likewise, if it is immutable it is obliged to return new vectors in those same methods. Which methods are
	 * affected is specified in the javadoc for those methods.
	 * @return whether this implementation is mutable or not
	 */
	public abstract boolean isMutable();

	
	/*
	 * Mutating or non-mutating depending on implementation.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Adds this vector with the input vector.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param vector the input vector
	 * @return the resulting vector
	 */
	public abstract BaseVector2 add(BaseVector2 vector);
	
	/**
	 * Adds this vector with the input vector.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return the resulting vector
	 */
	public abstract BaseVector2 add(double x, double y);
	
	/**
	 * Subtracts this vector with the input vector.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param vector the input vector
	 * @return the resulting vector
	 */
	public abstract BaseVector2 sub(BaseVector2 vector);
	
	/**
	 * Subtracts this vector with the input vector.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return the resulting vector
	 */
	public abstract BaseVector2 sub(double x, double y);
	
	/**
	 * Multiplies this vector with the input factor.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param m the multiplication factor
	 * @return the resulting vector
	 */
	public abstract BaseVector2 mult(double m);
	
	/**
	 * Divides this vector with the input divisor.
	 * Will likely fail if d is equal to zero, use mult() to divide instead if you want to be safer.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param d the divisor
	 * @return the resulting vector
	 */
	public abstract BaseVector2 div(double d);
	
	/**
	 * Returns a unit vector based on this vector.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * If this vectors length is 0 this method just returns a reference to this vector,
	 * this applies to both mutable and immutable implementations.
	 * @return the resulting vector
	 */
	public abstract BaseVector2 makeUnit();
	
	/**
	 * Returns a unit vector with the requested angle.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param degAngle the angle in degrees
	 * @return the resulting vector
	 */
	public abstract BaseVector2 makeUnit(double degAngle);
	
	/**
	 * Rotates this vector around its origin by the requested angle.
	 * Either mutates this vector or creates a new one depending on implementation.
	 * @param degAngle the angle in degrees
	 * @return the resulting vector
	 */
	public abstract BaseVector2 rotate(double degAngle);
	
	/*
	 * Non-mutating methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */

	/**
	 * Returns the length of this vector.
	 * @return length
	 */
	public double getLength() {
		return Math.sqrt(x*x + y*y);
	}

	/**
	 * Returns the angle between the x-axis and this vector.
	 * 0 <= angle < 360
	 * @return angle
	 */
	public double getAngle() {
		double angle = Math.atan2(y,x) * RAD_TO_DEG; //-180 < angle < 180
		if(angle < 0){ //Fixes angle so it becomes: 0 <= angle < 360
			angle += 360;
		}
		return angle;
	}

	/**
	 * Returns the distance from this vector to the input vector.
	 * @param destination the destination vector
	 * @return distance
	 */
	public double distance(BaseVector2 destination) {
		double distanceX = this.x - destination.getX();
		double distanceY = this.y - destination.getY();
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
	}

	/**
	 * Returns the distance from this vector to the input vector.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return distance
	 */
	public double distance(double x, double y) {
		double distanceX = this.x - x;
		double distanceY = this.y - y;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
	}

	/**
	 * Returns the distance squared from this vector to the input vector.
	 * Avoids the expensive "Math.sqrt()" method.
	 * @param destination the destination vector
	 * @return distance squared
	 */
	public double distanceSquared(BaseVector2 destination) {
		double distanceX = this.x - destination.getX();
		double distanceY = this.y - destination.getY();
		return distanceX * distanceX + distanceY * distanceY;
	}

	/**
	 * Returns the distance squared from this vector to the input vector.
	 * Avoids the expensive "Math.sqrt()" method.
	 * @param x the x coordinate of the input vector
	 * @param y the y coordinate of the input vector
	 * @return distance squared
	 */
	public double distanceSquared(double x, double y) {
		double distanceX = this.x - x;
		double distanceY = this.y - y;
		return distanceX * distanceX + distanceY * distanceY;
	}
	
	/**
	 * Returns the scalar product of this vector and the input vector.
	 * @param vector the input vector
	 * @return scalar product
	 */
	public double scalar(BaseVector2 vector) {
		return this.x*vector.x + this.y*vector.y;
	}
	
	/*
	 * Others
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Compares the length of this vector and the specified vector.
	 * Returns 0 if this vector and the specified vector has the same length.
	 * Returns a negative value if this vector is shorter than the specified vector.
	 * Returns a positive value if this vector is longer than the specified vector.
	 * @param vector the vector to compare this vector with
	 */
	@Override
	public int compareTo(BaseVector2 vector) {
		return Double.compare(this.getLength(), vector.getLength());
	}
	
	/**
	 * Returns a String describing this vector on the form "(x,y)".
	 * @return a string describing this vector.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(x).append(",").append(y).append(")");
		return builder.toString();
	}
	
	/**
	 * Two vectors are considered to be the same if they have the same x- and y-coordinate, regardless if they are mutable or not.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if (obj == null || !(obj instanceof BaseVector2)) {
			return false;
		}
		BaseVector2 other = (BaseVector2) obj;
		return Double.compare(this.x, other.x) == 0 && Double.compare(this.y, other.y) == 0;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		long xBits = Double.doubleToLongBits(this.x);
		long yBits = Double.doubleToLongBits(this.y);
		
		result = prime * result + (int) (xBits ^ (xBits >>> 32));
		result = prime * result + (int) (yBits ^ (yBits >>> 32));
		return result;
	}

	@Override
	public abstract BaseVector2 clone();
}
