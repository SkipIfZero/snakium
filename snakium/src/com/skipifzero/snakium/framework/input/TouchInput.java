package com.skipifzero.snakium.framework.input;

import java.util.List;

/**
 * An interface for getting touch input.
 * @author Peter Hillerström
 * @since 2013-07-06
 * @version 2
 */

public interface TouchInput {
	
	/**
	 * Updates the TouchEvents.
	 * Should be called first in every frame.
	 */
	public void update();
	
	/**
	 * Returns a list of active TouchEvents.
	 * @return a list of TouchEvents.
	 */
	public List<TouchEvent> getTouchEvents();
	
	/**
	 * Gets the scaling factor of this TouchInput.
	 * @return scaling factor
	 */
	public double getScalingFactor();
	
	/**
	 * Sets the scaling factor of this TouchInput
	 * @param scalingFactor
	 */
	public void setScalingFactor(double scalingFactor);
	
	/**
	 * Sets the scaling factor of this TouchInput with the help of a target width.
	 * @param targetWidth
	 */
	public void setScalingFactorTargetWidth(double targetWidth);
	
	/**
	 * Sets the scaling factor of this TouchInput with the help of a target height.
	 * @param targetHeight
	 */
	public void setScalingFactorTargetHeight(double targetHeight);
}
