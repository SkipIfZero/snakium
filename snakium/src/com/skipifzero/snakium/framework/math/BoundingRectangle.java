package com.skipifzero.snakium.framework.math;


/**
 * A BoundingShape implementation in the shape of a Rectangle.
 * 
 * @author Peter Hillerström
 * @since 2013-05-14
 * @version 1
 */
public class BoundingRectangle extends BaseBoundingShape {

	private double width, height;
	
	/**
	 * Creates a new BoundingRectangle with the specified position and size.
	 * @param x the center x-coordinate
	 * @param y the center y-coordinate
	 * @param width the width of the BoundingRectangle
	 * @param height the height of the BoundingRectangle
	 */
	public BoundingRectangle(double x, double y, double width, double height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Creates a new BoundingRectangle with the specified position and size.
	 * @param position the center position
	 * @param width the width of the BoundingRectangle
	 * @param height the height of the BoundingRectangle
	 */
	public BoundingRectangle(BaseVector2 position, double width, double height) {
		super(position);
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates a new BoundingRectangle with the specified position and size.
	 * @param position the center position
	 * @param dimension the dimension/size of the BoundingRectangle
	 */
	public BoundingRectangle(BaseVector2 position, BaseVector2 dimension) {
		super(position);
		this.width = dimension.getX();
		this.height = dimension.getY();
	}
	
	/**
	 * Returns the width of this BoundingRectangle.
	 * @return width of this BoundingRectangle
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of this BoundingRectangle.
	 * @return height of this BoundingRectangle
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns the dimensions of this BoundingRectangle as a new Vector2
	 * @return the dimensions of this BoundingRectangle.
	 */
	public BaseVector2 getDimension() {
		return new Vector2(this.width, this.height);
	}

	/**
	 * Sets the width of this BoundingRectangle to the specified width.
	 * @param width the specified width
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * Sets the height of this BoundingRectangle to the specified height.
	 * @param height the specified height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Sets the dimensions of this BoundingRectangle to the specified width & height.
	 * @param dimension the dimension/size of the BoundingRectangle
	 */
	public void setDimension(BaseVector2 dimension) {
		this.width = dimension.getX();
		this.height = dimension.getY();
	}

	@Override
	public boolean overlap(BaseVector2 point) {
		//Temp variables
		double halfWidth = width/2;
		double halfHeight = height/2;
		double rectX = getPosition().getX();
		double rectY = getPosition().getY();
		double pointX = point.getX();
		double pointY = point.getY();
		
		/*
		 * Pretty simple self-explanatory algorithm.
		 */
		return rectX - halfWidth <= pointX && rectX + halfWidth >= pointX &&
				rectY - halfHeight <= pointY && rectY + halfHeight >= pointY;
	}
	
	@Override
	public boolean overlap(BoundingShape shape) {
		
		if(shape instanceof BoundingRectangle) {
			return overlap((BoundingRectangle)shape);
		}
		
		if(shape instanceof BoundingCircle) {
			return overlap((BoundingCircle)shape);
		}
		
		//shape is of unknown type with no implemented overlap logic. Return false.
		return false;
	}
	
	/**
	 * Checks whether this BoundingRectangle overlaps with another BoundingRectangle.
	 * @param rect the other BoundingRectangle
	 * @return whether this BoundingRectangle overlaps with another BoundingRectangle
	 */
	public boolean overlap(BoundingRectangle rect) {
		//Temp variables
		double thisX = getPosition().getX();
		double thisY = getPosition().getY();
		double thisHalfWidth = width/2;
		double thisHalfHeight = height/2;
		double otherX = rect.getPosition().getX();
		double otherY = rect.getPosition().getY();
		double otherHalfWidth = rect.width/2;
		double otherHalfHeight = rect.height/2;
		
		/*
		 * Pretty simple algorithm if you think about it, but really hard to express in words,
		 * so I won't even try.
		 */
		return 	thisX - thisHalfWidth <= otherX + otherHalfWidth &&
				thisX + thisHalfWidth >= otherX - otherHalfWidth &&
				thisY - thisHalfHeight <= otherY + otherHalfHeight &&
				thisY + thisHalfHeight >= otherY - otherHalfHeight;
	}
	
	/**
	 * Checks whether this BoundingRectangle overlaps with the specified BoundingCircle.
	 * @param circle the specified BoundingCircle
	 * @return whether this BoundingRectangles overlaps with the specified BoundingCircle
	 */
	public boolean overlap(BoundingCircle circle) {
		/*
		 * If the length between the center of the circle and the closest point on the rectangle
		 * is less than or equal to the circles radius they overlap. Both sides of the equation is
		 * squared to avoid expensive sqrt() function. 
		 */
		
		//Temp variables
		double rectX = getPosition().getX();
		double rectY = getPosition().getY();
		double halfWidth = width/2;
		double halfHeight = height/2;
		double circleX = circle.getPosition().getX();
		double circleY = circle.getPosition().getY();
		double radius = circle.getRadius();
		
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
