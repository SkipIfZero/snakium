package com.skipifzero.snakium.framework.entities;

import com.skipifzero.snakium.framework.math.BaseVector2;

/**
 * Interface for a TextEntity.
 * 
 * @author Peter Hillerström
 * @since 2013-04-08
 * @version 1
 */
public interface TextEntity {
	
	/**
	 * Updates the Text, should be called once every frame.
	 * @param deltaTime the time since the last frame
	 */
	public void update(double deltaTime);
	
	/**
	 * Returns the text of this Text.
	 * @return text of this Text
	 */
	public String getText();
	
	/**
	 * Returns the position of this Text.
	 * @return position of this Text
	 */
	public BaseVector2 getPosition();
	
	/**
	 * Returns the size of this Text.
	 * @return size of this Text
	 */
	public double getSize();
	
	/**
	 * Returns the angle of this Text.
	 * @return angle of this Text
	 */
	public double getAngle();
	
	/**
	 * Returns the opacity of this Text.
	 * 0 <= opacity <= 1 where 1 is completely visible and 0 is completely transparent.
	 * @return opacity of this Text
	 */
	public double getOpacity();
	
	/**
	 * Returns the color of this Text.
	 * @return color of this Text
	 */
	public int getColor();
}
