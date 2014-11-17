package com.skipifzero.snakium.framework.math;


/**
 * A BoundingShape implementation in the shape of a Circle.
 * 
 * @author Peter Hillerström
 * @since 2013-03-27
 * @version 1
 */
public class BoundingCircle extends BaseBoundingShape {
	
	private double radius;
	
	/**
	 * Creates a new BoundingCircle with the specified center position and radius.
	 * @param x the center x-coordinate
	 * @param y the center y-coordinate
	 * @param radius the radius
	 */
	public BoundingCircle(double x, double y, double radius) {
		super(x, y);
		this.radius = radius;
	}
	
	/**
	 * Creates a new BoundingCircle with the specified center position and radius.
	 * @param position the center position
	 * @param radius the radius
	 */
	public BoundingCircle(BaseVector2 position, double radius) {
		super(position);
		this.radius = radius;
	}
	
	/**
	 * Returns this BoundingCircle's radius.
	 * @return radius
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Sets this BoundingCircle's radius to the specified radius
	 * @param radius the specified radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	@Override
	public boolean overlap(BaseVector2 point) {
		/*
		 * If the length from this circles center to the specified point is shorter than or equal to 
		 * the radius then this BoundingCircle overlaps the point. Both sides of the equation is 
		 * squared to avoid expensive sqrt() function.
		 */
		return getPosition().distanceSquared(point) <= radius*radius;
	}

	@Override
	public boolean overlap(BoundingShape shape) {
		
		if(shape instanceof BoundingCircle) {
			return overlap((BoundingCircle)shape);
		}
		
		if(shape instanceof BoundingRectangle) {
			return overlap((BoundingRectangle)shape);
		}
		
		//shape is of unknown type with no implemented overlap logic. Return false.
		return false;
	}
	
	/**
	 * Checks whether this BoundingCircle overlaps with another BoundingCircle
	 * @param circle the other BoundingCircle
	 * @return whether this BoundingCircle overlaps with another BoundingCircle
	 */
	public boolean overlap(BoundingCircle circle) {
		/*
		 * If the length between the center of the two circles is less than or equal to the the sum
		 * of the circle's radiuses they overlap. Both sides of the equation is squared to avoid
		 * expensive sqrt() function.
		 */
		double distanceSquared = getPosition().distanceSquared(circle.getPosition());
		double radiusSum = this.radius + circle.radius;
		return distanceSquared <= radiusSum*radiusSum;
	}
	
	/**
	 * Checks whether this BoundingCircle overlaps with the specified BoundingRectangle.
	 * @param rect the specified BoundingRectangle
	 * @return whether this BoundingCircle overlaps with the specified BoundingRectangle
	 */
	public boolean overlap(BoundingRectangle rect) {
		/*
		 * If the length between the center of the circle and the closest point on the rectangle
		 * is less than or equal to the circles radius they overlap. Both sides of the equation is
		 * squared to avoid expensive sqrt() function. 
		 */
		
		//Temp variables
		double rectX = rect.getPosition().getX();
		double rectY = rect.getPosition().getY();
		double halfWidth = rect.getWidth()/2;
		double halfHeight = rect.getHeight()/2;
		double circleX = getPosition().getX();
		double circleY = getPosition().getY();
		
		//Calculate closest point on rectangle
		double closestX = circleX;
		double closestY = circleY;
		
		if(circleX <= rectX - halfWidth) {
			closestX = rectX - halfWidth;
		} 
		else if(circleX >= rectX + halfWidth) {
			closestX = rectX + halfWidth;
		}
		
		if(circleY <= rectY - halfHeight) {
			closestY = rectY - halfHeight;
		}
		else if(circleY >= rectY + halfHeight) {
			closestY = rectY + halfHeight;
		}
		
		return getPosition().distanceSquared(closestX, closestY) <= radius*radius;
	}
}
