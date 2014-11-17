package com.skipifzero.snakium.framework.opengl;

import android.opengl.GLES10;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * A class used for setting the OpenGL viewport and setting the projection type to 2D.
 * Basically the size and position of this camera is relative to an internal coordinate system.
 * 
 * @author Peter Hillerström
 * @since 2013-04-28
 * @version 2
 */
public class Camera2D {
	
	private final BoundingRectangle bounds;
	
	/**
	 * Creates a Camera with the specified size and position.
	 * @param centerX the center position on the x-axis
	 * @param centerY the center position on the y-axis
	 * @param frustrumX the width of the camera
	 * @param frustrumY the height of the camera
	 */
	public Camera2D(double centerX, double centerY, double frustrumX, double frustrumY) {
		this.bounds = new BoundingRectangle(centerX, centerY, frustrumX, frustrumY);
	}
	
	/**
	 * Moves the camera and initializes various OpenGL stuff so you won't have to the manually.
	 * Must be run first each frame before using SpriteBatcher.
	 * @param viewWidth the width of the GLSurfaceView
	 * @param viewHeight the height of the GLSurfaceView
	 */
	public void initialize(int viewWidth, int viewHeight) {
		GLES10.glViewport(0, 0, viewWidth, viewHeight);
		GLES10.glMatrixMode(GLES10.GL_PROJECTION);
		GLES10.glLoadIdentity();
		
		float halfFrustrumX = (float)bounds.getWidth()/2;
		float halfFrustrumY = (float)bounds.getHeight()/2;
		float xPos = (float)bounds.getPosition().getX();
		float yPos = (float)bounds.getPosition().getY();
		GLES10.glOrthof(xPos - halfFrustrumX, xPos + halfFrustrumX, yPos - halfFrustrumY, yPos + halfFrustrumY, 1, -1);
		
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
		GLES10.glLoadIdentity();
	}
	
	/**
	 * Changes size of the camera.
	 * @param frustrumX
	 * @param frustrumY
	 */
	public void setSize(double frustrumX, double frustrumY) {
		bounds.setWidth(frustrumX);
		bounds.setHeight(frustrumY);
	}
	
	/**
	 * Sets the position of this camera.
	 * @param position the position
	 */
	public void setPosition(BaseVector2 position) {
		bounds.setPosition(position);
	}
	
	/**
	 * Gets the position of this camera.
	 * Returns a reference to the internal position. Be careful.
	 * @return position of this camera
	 */
	public Vector2 getPosition() {
		return bounds.getPosition();
	}
	
	/**
	 * Gets the position of this camera.
	 * Returns a copy of the internal position, more safe.
	 * @return position of this cammera
	 */
	public Vector2 getPositionCopy() {
		return bounds.getPosition().clone();
	}
	
	/**
	 * Returns a direct reference to this Camera2D's internal BoundingRectangle. Can be used to
	 * change the cameras position and size or to check if this camera overlaps with stuff.
	 * @return reference to this Camera2D's internal BoundingRectangle
	 */
	public BoundingRectangle getBounds() {
		return bounds;
	}
	
	public double getX() {
		return bounds.getPosition().getX();
	}
	
	public double getY() {
		return bounds.getPosition().getY();
	}
	
	public double getWidth() {
		return bounds.getWidth();
	}
	
	public double getHeight() {
		return bounds.getHeight();
	}
}
