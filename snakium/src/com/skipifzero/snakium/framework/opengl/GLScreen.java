package com.skipifzero.snakium.framework.opengl;

/**
 * Interface for a screen to be used with a GLActivity.
 * 
 * A recommendation is to make sure the implementation of this interface gets an instance of the
 * GLActivity it is running in. Otherwise it will be impossible to do stuff like checking if the
 * back button is pressed, or switch to other GLScreens.
 * 
 * @author Peter Hillerström
 * @since 2013-07-12
 * @version 2
 */
public interface GLScreen {

	/**
	 * Called once each frame.
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void update(double deltaTime, int fps);
	
	/**
	 * Should be called once each frame from the current GLControllers update method.
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void draw(double deltaTime, int fps);
	
	/**
	 * Called when Activity is resumed and when this GLController is created/resumed.
	 */
	public void onResume();
	
	/**
	 * Called when Activity is paused and before it is destroyed. DONT_PAUSE_ASSETS is only sent
	 * when a normal "changeGLScreen()" is used.
	 */
	public void onPause(PauseType pauseType);
	
	/**
	 * Called when Activity is destroyed and when this GLController is destroyed.
	 */
	public void dispose();
	
	/**
	 * Whether this GLController should catch the back key or not.
	 * @return whether this GLController should catch the back key or not
	 */
	public boolean catchBackKey();
	
	
	/**
	 * A simple enum used to specify what type of "onPause()" was called. In a "Normal" pause the
	 * assets should be paused. But in a "Dont Pause Assets" pause the logic should be paused, but
	 * not the assets. One exception is music which should probably still be paused.
	 * 
	 * @author Peter Hillerström
	 * @since 2013-07-12
	 * @version 1
	 */
	public static enum PauseType {
		NORMAL, DONT_PAUSE_ASSETS;
	}
}
