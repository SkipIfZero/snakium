package com.skipifzero.snakium.framework.math;

import java.util.Collection;


/**
 * Abstract base class for BoundingShapes.
 * 
 * @author Peter Hillerström
 * @since 2013-03-26
 * @version 1
 */
public abstract class BaseBoundingShape implements BoundingShape {

	private final Vector2 position;
	
	public BaseBoundingShape(double x, double y) {
		this.position = new Vector2(x, y);
	}
	
	public BaseBoundingShape(BaseVector2 position) {
		this.position = new Vector2(position);
	}
	
	@Override
	public Vector2 getPosition() {
		return position;
	}
	
	@Override
	public double getX() {
		return position.getX();
	}
	
	@Override
	public double getY() {
		return position.getY();
	}
	
	@Override
	public void setPosition(BaseVector2 position) {
		this.position.set(position);
	}
	
	@Override
	public boolean overlap(Collection<? extends BoundingShape> collection) {
		for(BoundingShape shape : collection) {
			if(overlap(shape)) {
				return true;
			}
		}
		return false;
	}
}
