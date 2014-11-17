package com.skipifzero.snakium.framework.math;

import java.util.Collection;


/**
 * Interface for different mutable 2-dimensional bounding shapes.
 * 
 * Mainly meant for circles and rectangles, but other shapes, like triangles, are also possible.
 * 
 * @author Peter Hillerström
 * @since 2013-03-26
 * @version 1
 */
public interface BoundingShape {
	
	/**
	 * Returns this BoundingShape's center position.
	 * @return this BoundingShape's center position
	 */
	public Vector2 getPosition();
	
	/**
	 * Returns the x-coordinate of this BoundingShape's center position.
	 * @return x-coordinate of this BoundingShape's center position
	 */
	public double getX();
	
	/**
	 * Returns the y-coordinate of this BoundingShape's center position.
	 * @return y-coordinate of this BoundingShape's center position
	 */
	public double getY();
	
	/**
	 * Sets this BoundingShape's center position.
	 * @param position the position to set
	 */
	public void setPosition(BaseVector2 position);
	
	/**
	 * Checks whether this BoundingShape contains the specified point.
	 * @param point the specified point
	 * @return whether this BoundingShape contains the specified point
	 */
	public boolean overlap(BaseVector2 point);
	
	/**
	 * Checks if this BoundingShape and the specified BoundingShape overlaps.
	 * In the case that overlap checks haven't been implemented between two BoundingShape
	 * implementations this returns false.
	 * @param shape the BoundingShape to compare this BoundingShape with
	 * @return true if this BoundingShape and the specified BoundingShape overlaps
	 */
	public boolean overlap(BoundingShape shape);
	
	/**
	 * Checks if this BoundingShape overlaps with any BoundingShape in the specified Collection.
	 * In the case that overlap checks haven't been implemented between two BoundingShape
	 * implementations the result of that operation will be false.
	 * @param collection the Collection with BoundingShapes
	 * @return true if this BoundingShape overlaps with any BoundingShape in the specified Collection.
	 */
	public boolean overlap(Collection<? extends BoundingShape> collection);
}
