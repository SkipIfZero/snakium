package com.skipifzero.snakium.framework;

import java.util.ArrayList;
import java.util.List;

import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.input.TouchEvent.TouchType;
import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * A class used for creating touch based buttons.
 * 
 * One important thing to note is that a TouchButton can be both touched and activated. The touched
 * state is only meant to be used when drawing the button, while the activated state should be used
 * when you want the button to do something.
 * 
 * There is also a third state, the enabled state. When a TouchButton is enabled it can be used as
 * usual, but when it's disabled the touched and activated states are always false. A TouchButton
 * is always enabled by default.
 * 
 * There are three types of activation types:
 * 
 * ACTIVATE_ON_TOUCH: Button will activate if a TOUCH_DOWN event that overlaps with this TouchButton
 * exists.
 *  
 * ACTIVATE_WHILE_TOUCHED: Button will activate if it is touched.
 * 
 * ACTIVATE_ON_RELEASE: Button will activate if a TOUCH_UP event that overlaps with this TouchButton
 * exists.
 * 
 * @author Peter Hillerström
 * @since 2013-05-09
 * @version 2
 */
public class TouchButton {
	
	/**
	 * Interface for listener to be called when a TouchButton is activated.
	 * @author Peter Hillerström
	 * @since 2013-04-05
	 * @version 1
	 */
	public interface TouchButtonListener {
		/**
		 * Called each frame one of the TouchButton's this listener is registered to is activated.
		 * @param source the TouchButton that is activated
		 */
		public void onButtonActivated(TouchButton source);
	}
	
	/**
	 * Enum to determine type of TouchButton.
	 */
	public enum TouchButtonType {
		ACTIVATE_ON_TOUCH, ACTIVATE_WHILE_TOUCHED, ACTIVATE_ON_RELEASE;
	}
		
	private final List<TouchButtonListener> listeners;
	
	private final TouchButtonType type;
	private final BoundingRectangle bounds;
	private boolean enabled, touched, activated;
	
	//Temp variables
	private final Vector2 tempVector = new Vector2(-1,-1);
	private final List<TouchEvent> currentlyTouching = new ArrayList<TouchEvent>(10);
	
	/**
	 * Creates a new TouchButton of the specified type with the specified position and size.
	 * @param position the specified position
	 * @param width the specified width
	 * @param height the specified height
	 * @param type the specified type
	 */
	public TouchButton(BaseVector2 position, double width, double height, TouchButtonType type) {
		this.listeners = new ArrayList<TouchButtonListener>();
		this.bounds = new BoundingRectangle(position, width, height);
		this.type = type;
		this.enabled = true;
		this.touched = false;
		this.activated = false;
	}
	
	/**
	 * Creates a new TouchButton of the specified type with the specified position and size.
	 * @param x the specified x-coordinate
	 * @param y the specified y-coordinate
	 * @param width the specified width
	 * @param height the specified height
	 * @param type the specified type
	 */
	public TouchButton(double x, double y, double width, double height, TouchButtonType type) {
		this.listeners = new ArrayList<TouchButtonListener>();
		this.bounds = new BoundingRectangle(x, y, width, height);
		this.type = type;
		this.enabled = true;
		this.touched = false;
		this.activated = false;
	}
	
	/**
	 * Updates this TouchButton's activated and touched states depending on the list of TouchEvents.
	 * Should be called every frame.
	 * @param touchEvents the list of TouchEvents.
	 * @return whether this button is currently activated or not
	 */
	public boolean update(List<TouchEvent> touchEvents) {
		//If button is disabled it's neither touched or activated.
		if(!enabled) {
			touched = false;
			activated = false;
			return false;
		}
		
		//Null checks.
		if(touchEvents == null) {
			touched = false;
			activated = false;
			return false;
		}
		
		//Checks if button is touched
		touched = false;
		for(TouchEvent event : touchEvents) {
			if(bounds.overlap(event.getPosition(tempVector))) {
				touched = true;
				currentlyTouching.add(event);
			}
		}
		
		//Check if button is activated
		activated = false;
		if(touched) { //If button isn't touched it's not activated.
			switch(type) {
				
				case ACTIVATE_ON_TOUCH:
					for(TouchEvent event : currentlyTouching) {
						if(event.getType() == TouchType.TOUCH_DOWN) {
							activated = true;
							break;
						}
					}
					break;
					
				case ACTIVATE_WHILE_TOUCHED:
					activated = true;
					break;
					
				case ACTIVATE_ON_RELEASE:
					for(TouchEvent event : currentlyTouching) {
						if(event.getType() == TouchType.TOUCH_UP) {
							activated = true;
							break;
						}
					}
					break;
				
				default:
					throw new AssertionError(); //Should never happen.
			}
		}
		
		currentlyTouching.clear();
		
		//Notifies listeners if this TouchButton is activated this frame
		if(activated) {
			for(TouchButtonListener listener : listeners) {
				listener.onButtonActivated(this);
			}
		}
		
		return activated;
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns whether this TouchButton is enabled or not.
	 * @return whether this TouchButton is enabled or not
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Returns whether this TouchButton is touched or not.
	 * @return whether this TouchButton is touched or not
	 */
	public boolean isTouched() {
		return touched;
	}
	
	/**
	 * Returns whether this TouchButton is activated or not.
	 * @return whether this TouchButton is activated or not
	 */
	public boolean isActivated() {
		return activated;
	}
	
	/**
	 * Adds the specified TouchButtonListener to the list of listeners to be notified every frame
	 * this TouchButton is activated.
	 * @param listener the specified TouchButtonListener
	 */
	public void addTouchButtonListener(TouchButtonListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the specified TouchButtonListener from the list of listeners to be notified every
	 * frame this TouchButton is activated.
	 * @param listener the specified TouchButtonListener
	 */
	public void removeTouchButtonListener(TouchButtonListener listener) {
		listeners.remove(listener);
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns this TouchButton's type.
	 * @return this TouchButton's type
	 */
	public TouchButtonType getTouchButtonType() {
		return type;
	}
	
	/**
	 * Returns this TouchButton's position.
	 * @return this TouchButton's position.
	 */
	public Vector2 getPosition() {
		return bounds.getPosition();
	}
	
	/**
	 * Returns this TouchButton's width.
	 * @return this TouchButton's width
	 */
	public double getWidth() {
		return bounds.getWidth();
	}
	
	/**
	 * Returns this TouchButton's height.
	 * @return this TouchButton's height
	 */
	public double getHeight() {
		return bounds.getHeight();
	}

	/**
	 * Returns this TouchButton's dimensions.
	 * @return this TouchButton's dimensions.
	 * @see BoundingRectangle#getDimension()
	 */
	public BaseVector2 getDimension() {
		return bounds.getDimension();
	}

	/**
	 * Returns this TouchButton's BoundingRectangle.
	 * @return this TouchButton's BoundingRectangle
	 */
	public BoundingRectangle getBounds() {
		return bounds;
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Enables this TouchButton.
	 */
	public void enable() {
		enabled = true;
	}
	
	/**
	 * Disables this TouchButton. Until it has been enabled again it can't be touched or activated.
	 */
	public void disable() {
		enabled = false;
		touched = false;
		activated = false;
	}
	
	/**
	 * Sets this TouchButton's position to the specified position.
	 * @param position the specified position
	 */
	public void setPosition(BaseVector2 position) {
		bounds.setPosition(position);
	}
	
	/**
	 * Sets this TouchButton's width to the specified width.
	 * @param width the specified width
	 */
	public void setWidth(double width) {
		bounds.setWidth(width);
	}
	
	/**
	 * Sets this TouchButton's height to the specified height.
	 * @param height the specified height
	 */
	public void setHeight(double height) {
		bounds.setHeight(height);
	}

	/**
	 * Sets this TouchButton's dimensions to the specified dimensions.
	 * @param dimension the specified dimension
	 * @see BoundingRectangle#setDimension(BaseVector2)
	 */
	public void setDimension(BaseVector2 dimension) {
		bounds.setDimension(dimension);
	}
}