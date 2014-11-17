package com.skipifzero.snakium.framework.math;

/**
 * An immutable 2 dimensional Vector2 implementation.
 * The instance variables are protected, therefore it is important that this class is placed in its own 
 * package together with the other vectors classes. Otherwise someone might be able to mutate objects of
 * this class.
 * @author Peter Hillerström
 * @since 2013-04-28
 * @version 2
 */
public final class FinalVector2 extends BaseVector2 {

	/*
	 * Constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public FinalVector2(BaseVector2 vector) {
		super(vector);
	}
	
	public FinalVector2(double x, double y) {
		super(x, y);
	}
	
	/*
	 * Miscellaneous
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public boolean isMutable() {
		return false;
	}

	/*
	 * Non-mutating methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public FinalVector2 add(BaseVector2 vector) {
		return new FinalVector2(this.x + vector.x, this.y + vector.y);
	}

	@Override
	public FinalVector2 add(double x, double y) {
		return new FinalVector2(this.x + x, this.y + y);
	}

	@Override
	public FinalVector2 sub(BaseVector2 vector) {
		return new FinalVector2(this.x - vector.x, this.y - vector.y);
	}

	@Override
	public FinalVector2 sub(double x, double y) {
		return new FinalVector2(this.x - x, this.y - y);
	}

	@Override
	public FinalVector2 mult(double m) {
		return new FinalVector2(this.x * m, this.y * m);
	}
	
	@Override
	public FinalVector2 div(double d) {
		return new FinalVector2(this.x / d, this.y / d);
	}

	@Override
	public FinalVector2 makeUnit() {
		double length = this.getLength();
		if(length != 0){ //Checks if length is 0 to avoid division by 0.
			return new FinalVector2(this.x / length, this.y / length);
		}
		return this;
	}

	@Override
	public FinalVector2 makeUnit(double degAngle) {
		double radAngle = degAngle * DEG_TO_RAD;
		return new FinalVector2(Math.cos(radAngle), Math.sin(radAngle));
	}

	@Override
	public FinalVector2 rotate(double degAngle) {
		double radAngle = degAngle * DEG_TO_RAD;
		double cos = Math.cos(radAngle);
		double sin = Math.sin(radAngle);
		
		double tempX = this.x * cos - this.y * sin;
		double tempY = this.x * sin + this.y * cos;
		
		return new FinalVector2(tempX, tempY);
	}
	
	/*
	 * Others
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public FinalVector2 clone() {
		return this; //FinalVector2 is immutable, can safely return a reference to this object.
	}
}
