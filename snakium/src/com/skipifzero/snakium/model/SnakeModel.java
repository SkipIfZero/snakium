package com.skipifzero.snakium.model;

import java.util.List;

/**
 * An interface used to implement Snake models.
 * 
 * Makes it possible to implement many different versions of Snake and display them in the same
 * view. Implementations are free to do more or less whatever they want. For example "bonus time"
 * usually refers to bonus object that appears every now and then, but a creative model could use
 * it for things like mirroring the board, make the Snake go faster, make multiple objects appear,
 * change the size of the board, etc.
 * 
 * @author Peter Hillerström
 * @since 2013-06-25
 * @version 1
 */
public interface SnakeModel {
	
	/**
	 * Updates the SnakeModel. View is obliged to call this once every frame. View is also obliged
	 * to continue updating the model even if isGameOver() returns true, this to allow animations
	 * after death.
	 * @param deltaTime the time in seconds since the last frame
	 * @param inputDirection the current input direction
	 */
	public void update(double deltaTime, SnakeDirection inputDirection);
	
	/**
	 * Returns a list of SnakeTile's for the view to draw. Should include the Snake and the objects.
	 * Worth to note is that the view is not obliged to and probably won't draw SnakeTile's with the
	 * Type EMPTY. In fact, the view can draw them as whatever it likes so they shouldn't be
	 * included in this list.
	 * @return a list of all SnakeTile's to draw
	 */
	public List<SnakeTile> getSnakeTiles();
	
	/**
	 * Returns the progress between two tiles.
	 * 0 <= progress < 1
	 * Where 0 is the moment the Snake ha entered a new tile and 1 is the moment before it exits.
	 * @return progress between two tiles.
	 */
	public double getProgress();
	
	/**
	 * Returns the width of the board in tiles.
	 * Is not necessarily static and can change at any time during a game.
	 * @return width of the board in tiles
	 */
	public int getBoardWidth();
	
	/**
	 * Returns the height of the board in tiles.
	 * Is not necessarily static and can change at any time during a game.
	 * @return height of the board in tiles
	 */
	public int getBoardHeight();
	
	/**
	 * Returns bonus time left measured in tiles.
	 * Bonus time is considered activated if result of this method is > 0.
	 * @return bonus time left measured in tiles
	 */
	public int bonusTimeLeft();
	
	/**
	 * Returns the current score accumulated during this game.
	 * @return current score
	 */
	public int getScore();
	
	/**
	 * Returns the score gained from the current tile in the board. 
	 * 
	 * This method has pretty strict conditions. It may only return the score gained from a tile
	 * ONCE, and it should do it as soon as possible after the Snake has entered a new tile that
	 * awards points. Otherwise it should return 0 or lower.
	 * 
	 * If this method always returns 0 or lower the view is not allowed to draw or display this
	 * info in any way whatsoever. Making it effectively possible to disable this feature.
	 * 
	 * It is worth noting that if you have an insane Snake model where the speed of the snake
	 * (measured in tiles) approaches the fps of the device you shouldn't use this feature as it
	 * will become unpredictable.
	 * 
	 * @return score gained from current tile
	 */
	public int getCurrentTileScore();
	
	/**
	 * Returns true if game is over. View is obliged to continue updating
	 * @return whether game is over or not
	 */
	public boolean isGameOver();
}
