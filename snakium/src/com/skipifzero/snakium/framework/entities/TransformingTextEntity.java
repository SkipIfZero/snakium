package com.skipifzero.snakium.framework.entities;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * An implementation of the TextEntity interface. Used to create a TextEntity that transforms from
 * one TextEntity to another. Only takes position, size and angle into account when transforming.
 * 
 * Designed in such a way that it can easily be reused or "pooled" to avoid making the garbage
 * collector angry.
 * 
 * @author Peter Hillerström
 * @since 2013-04-11
 * @version 1
 */
public class TransformingTextEntity implements TextEntity {

	private final StaticTextEntity originText = new StaticTextEntity();
	private final StaticTextEntity destinationText = new StaticTextEntity();
	private final StaticTextEntity currentText = new StaticTextEntity();
	private double timeBeforeTransform, transformTime, transpiredTime;
	
	//Extra variables
	private double currentTextSize;
	
	//Difference
	private final Vector2 posDiff = new Vector2(0,0);
	private double sizeDiff, angleDiff;
	
	//Temp variables
	private final Vector2 tempVec = new Vector2(0,0);
	
	/*
	 * Constructor
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Creates a new TransformingTextEntity, but it needs to be set before it can be used.
	 */
	public TransformingTextEntity() {
	}
	
	/*
	 * Setters (Semi-constructors)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets this TransformingTextEntity to the specified values.
	 * @param text the text
	 * @param origin the origin TextEntity
	 * @param destination the destination TextEntity
	 * @param color the color of the text
	 * @param timeBeforeTransform the time before transformation starts
	 * @param transformTime the time it takes the transformation to complete
	 * @return this
	 */
	public TransformingTextEntity set(String text, TextEntity origin, TextEntity destination, int color, double timeBeforeTransform, double transformTime) {
		originText.set(origin);
		destinationText.set(destination);
		currentText.set(origin);
		currentText.setText(text);
		currentText.setColor(color);
		this.timeBeforeTransform = timeBeforeTransform;
		this.transformTime = transformTime;
		this.transpiredTime = 0;
		
		this.currentTextSize = currentText.getSize();
		
		//Position difference
		this.posDiff.set(tempVec.set(destinationText.getPosition())).sub(originText.getPosition()).mult(1/transformTime);
		///Size difference
		this.sizeDiff = (destinationText.getSize() - originText.getSize())/transformTime;
		//Angle difference
		double angleDiff1 = destinationText.getAngle() - originText.getAngle();
		double angleDiff2;
		if(angleDiff1 < 0) {
			angleDiff2 = 360 + angleDiff1;
		} else {
			angleDiff2 = angleDiff1 - 360;
		}
		if(Math.abs(angleDiff1) < Math.abs(angleDiff2)) {
			this.angleDiff = angleDiff1/transformTime;
		} else {
			this.angleDiff = angleDiff2/transformTime;
		}
		
		return this;
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns whether this TransformingTextEntity is done transforming or not.
	 * @return whether this TransformingTextEntity is done transforming or not
	 */
	public boolean isDoneTransforming() {
		return timeBeforeTransform + transformTime <= transpiredTime;
	}
	
	/*
	 * Methods inherited from TextEntity
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime) {
		transpiredTime += deltaTime;
		
		if(transpiredTime <= timeBeforeTransform || transpiredTime > timeBeforeTransform + transformTime) {
			return;
		}
	
		//Position
		tempVec.set(posDiff).mult(deltaTime);
		currentText.setPosition(currentText.getPosition().add(tempVec));
		//Size
		this.currentTextSize += sizeDiff*deltaTime;
		currentText.setSize((int)Math.round(currentTextSize));
		//Angle
		currentText.setAngle(currentText.getAngle() + angleDiff*deltaTime);
	}

	@Override
	public String getText() {
		return currentText.getText();
	}

	@Override
	public BaseVector2 getPosition() {
		return currentText.getPosition();
	}

	@Override
	public double getSize() {
		return currentText.getSize();
	}

	@Override
	public double getAngle() {
		return currentText.getAngle();
	}

	@Override
	public double getOpacity() {
		return currentText.getOpacity();
	}

	@Override
	public int getColor() {
		return currentText.getColor();
	}
}
