package com.skipifzero.snakium.framework.opengl;

/**
 * An interface for OpenGL textures.
 * @author Peter Hillerström
 * @version 1
 */
public interface Texture {
	
	/**
	 * Binds this texture.
	 */
	public void bind();
	
	/**
	 * Unbinds this texture.
	 */
	public void unbind();
	
	/**
	 * Activates or deactivates smoothing.
	 * @param smoothing
	 */
	public void setSmoothing(boolean smoothing);
	
	/**
	 * Whether this Texture has smoothing activated or not.
	 * @return smoothing
	 */
	public boolean isSmoothed();
	
	/**
	 * Disposes of this Texture.
	 */
	public void dispose();
	
	/**
	 * Returns width of this texture in pixels
	 * @return width in pixels
	 */
	public int getWidth();
	
	/**
	 * Returns height of this texture in pixels
	 * @return height in pixels
	 */
	public int getHeight();
}
