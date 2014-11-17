package com.skipifzero.snakium.framework.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.skipifzero.snakium.framework.input.Pool.PoolObjectFactory;
import com.skipifzero.snakium.framework.input.TouchEvent.TouchType;

/**
 * An implementation of the TouchInput interface.
 * This implementation pools TouchEvents, which means that it reuses them. Thus you should never directly store references to TouchEvents
 * returned from this implementation, if you really need to store a specific TouchEvent you can just clone it.
 * 
 * @author Peter Hillerström
 * @since 2013-07-06
 * @version 2
 */

public class PooledTouchInput implements TouchInput, OnTouchListener {
	
	private final View view;
	private double scalingFactor;
	private final int maxTouchPositions;
	
	private double viewY = 0;
	private double viewX = 0;
	
	//Pool used for recycling TouchEvents.
	private Pool<TouchEvent> touchEventPool;
	
	//Various lists containing TouchEvents.
	private List<TouchEvent> filteredEvents = new ArrayList<TouchEvent>(); //The final TouchEvents that are returned through "getTouchEvents()".
	private List<TouchEvent> bufferedEvents = new ArrayList<TouchEvent>(); //All the TouchEvents that occurred since the last frame.
	private List<TouchEvent> tempEvents = new ArrayList<TouchEvent>(); //Temporary storage of TouchEvents while creating the filteredEvents list.
	
	//Pointer State variables.
	private boolean[] touchDown;
	private boolean[] touchUp;
	private boolean[] touchDragged;
	
	//Temporary variables
	private TouchEvent tempEvent;
	private double tempX, tempY;
	
	public PooledTouchInput(View view, int maxTouchPositions){
		this(view, 1, maxTouchPositions);
	}
	
	/**
	 * Creates a new PooledInput instance.
	 * @param view the view to get touch events from.
	 * @param scalingFactor (Example: screenX * scaleFactor = otherX)
	 * @param maxTouchPositions
	 */
	public PooledTouchInput(View view, double scalingFactor, int maxTouchPositions){
		this.view = view;
		this.scalingFactor = scalingFactor;
		this.maxTouchPositions = maxTouchPositions;
		
		view.setOnTouchListener(this);
		
		//Creates TouchEvent Pool.
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>(){
			@Override
			public TouchEvent createObject(){
				return new TouchEvent(-1, -1, -1, TouchType.NOT_TOUCHING);
			}
		};		
		touchEventPool = new Pool<TouchEvent>(factory, 40);
		
		//Creates state arrays.
		touchDown = new boolean[maxTouchPositions];
		Arrays.fill(touchDown, false);
		touchUp = new boolean[maxTouchPositions];
		Arrays.fill(touchUp, false);
		touchDragged = new boolean[maxTouchPositions];
		Arrays.fill(touchDragged, false);
	}
		
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		synchronized(this){
			int action = motionEvent.getAction() & MotionEvent.ACTION_MASK; //Type of action that occurred.
			int pointerIndex = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT; //PointerIndex, used in MotionEvent classes.
			int pointerId = motionEvent.getPointerId(pointerIndex); //Pointer identifier. The variable known as "pointer" in other methods.
		
			//Checks if input is "out of bounds".
			if(pointerId >= maxTouchPositions){
            	return true;
            }
			
			//Temporary variables and references.
			tempEvent = null;
			tempX = -1;
			tempY = -1;
			
			switch(action){
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					//Creates TouchEvent based on the MotionEvent.
					tempX = fixBoundsX(motionEvent.getX(pointerIndex)*scalingFactor);
					tempY = fixBoundsY((viewY - motionEvent.getY(pointerIndex))*scalingFactor);
					tempEvent = getTouchEvent(pointerId, tempX, tempY, TouchType.TOUCH_DOWN);
					bufferedEvents.add(tempEvent);

					//Sets state variables.
					if(!touchUp[pointerId]){
						touchDown[pointerId] = true;
					}
					touchDragged[pointerId] = true;
					break;
					
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					//Creates TouchEvent based on the MotionEvent.
					tempX = fixBoundsX(motionEvent.getX(pointerIndex)*scalingFactor);
					tempY = fixBoundsY((viewY - motionEvent.getY(pointerIndex))*scalingFactor);
					tempEvent = getTouchEvent(pointerId, tempX, tempY, TouchType.TOUCH_UP);
					bufferedEvents.add(tempEvent);
						
					//Sets state variables.
					touchDown[pointerId] = false;
					touchUp[pointerId] = true;
					touchDragged[pointerId] = false;
					break;
					
				case MotionEvent.ACTION_MOVE:
					int pointerCount = motionEvent.getPointerCount();
					for(int i = 0; i < pointerCount; i++){
						pointerIndex = i;
						pointerId = motionEvent.getPointerId(pointerIndex);
						
						//Checks if pointer is "out of bounds".
						if(pointerId >= maxTouchPositions){
							continue;
			            }
						
						//Creates TouchEvent based on the MotionEvent.
						tempX = fixBoundsX(motionEvent.getX(pointerIndex)*scalingFactor);
						tempY = fixBoundsY((viewY - motionEvent.getY(pointerIndex))*scalingFactor);
						tempEvent = getTouchEvent(pointerId, tempX, tempY, TouchType.TOUCH_DRAGGED);
						bufferedEvents.add(tempEvent);
						
						//Sets state variables.
						if(!touchDown[pointerId]){
							touchDragged[pointerId] = true;
						}
					}
					break;
					
				default:
					throw new AssertionError(); //I don't think this can happen. I hope.
					
			}
			return true;	
		}
	}
	
	/**
	 * Updates input.
	 * Filters the TouchEvent that has occurred since the last frame.
	 * Also recycles old objects.
	 */
	@Override
	public void update() {
		view.setOnTouchListener(this); //TODO: Yeah... This is a hack. Hope it's not expensive.
		
		synchronized(this){	
			
			//Sets viewX and viewY.
			viewX = view.getWidth();
			viewY = view.getHeight();
			
			tempEvent = null;
			
			/*
			 * Yes, this bit of code is ugly and hard to read.
			 * What is basically does is that it selects and removes one event per pointer from the buffered events and places
			 * it in a temporary list.
			 */
			for(int p = 0; p < maxTouchPositions; p++){
				
				if(touchDown[p]){
				
					for(int i = 0; i < bufferedEvents.size(); i++){
						tempEvent = bufferedEvents.get(i);
						if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_DOWN){
							tempEvents.add(bufferedEvents.remove(i));
							break;
						}
					}
					
				}else if(touchUp[p]){
					
					for(int i = 0; i < bufferedEvents.size(); i++){
						tempEvent = bufferedEvents.get(i);
						if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_UP){
							tempEvents.add(bufferedEvents.remove(i));
							break;
						}
					}
					
				/*
				 * Here we do some ugly stuff. Basically I want the latest TOUCH_DRAGGED event that have occurred.
				 * And if no such event has occurred, I want the corresponding event from the previous frame.
				 * If that event doesn't exist, I "convert" a TOUCH_DOWN event from the previous frame to one.
				 * If it still doesn't work something very weird has happened, and I simply ignore it and pretend
				 * like nothing happened.
				 */
				}else if(touchDragged[p]){
					
					int latestIndex = -1;
					
					//Tries to get the index for the latest TOUCH_DRAGGED event.
					for(int i = 0; i < bufferedEvents.size(); i++){
						tempEvent = bufferedEvents.get(i);
						if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_DRAGGED){
							latestIndex = i;
						}
					}
					
					//If index is acquired gets the event and jumps to the next pointer.
					if(latestIndex != -1){
						tempEvents.add(bufferedEvents.remove(latestIndex));
						continue;
					}
					
					//Tries to get the index for the previous TOUCH_DRAGGED event.
					if(latestIndex == -1){
						for(int i = 0; i < filteredEvents.size(); i++){
							tempEvent = filteredEvents.get(i);
							if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_DRAGGED){
								latestIndex = i;
							}
						}
					}
					
					//If index is acquired gets the event and jumps to the next pointer.
					if(latestIndex != -1){
						tempEvents.add(filteredEvents.remove(latestIndex));
						continue;
					}
					
					//Checks if there exists a previous TOUCH_DOWN event for this pointer. If it does "convert" it to a TOUCH_DRAGGED event and add it to the temp list.
					if(latestIndex == -1){
						for(int i = 0; i < filteredEvents.size(); i++){
							tempEvent = filteredEvents.get(i);
							if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_DOWN){
								tempEvent = getTouchEvent(tempEvent.getPointer(), tempEvent.getX(), tempEvent.getY(), TouchType.TOUCH_DRAGGED);
								tempEvents.add(tempEvent);
								latestIndex = -2; //Hack to make sure next thing doesn't run.
								break;
							}
						}
					}
					
					//Last resort: Checks if there exists a previous TOUCH_UP event for this pointer. If it does "convert" it to a TOUCH_DRAGGED EVENT and add it to the temp list.
					if(latestIndex == -1){
						for(int i = 0; i < filteredEvents.size(); i++){
							tempEvent = filteredEvents.get(i);
							if(tempEvent.getPointer() == p && tempEvent.getType() == TouchType.TOUCH_UP){
								tempEvent = getTouchEvent(tempEvent.getPointer(), tempEvent.getX(), tempEvent.getY(), TouchType.TOUCH_DRAGGED);
								tempEvents.add(tempEvent);
								break;
							}
						}
					}
				}
			}
			
			Arrays.fill(touchUp, false);
			Arrays.fill(touchDown, false);
			
			//Frees filtered events from previous frame.
			for(int i = 0; i < filteredEvents.size(); i++){		
				touchEventPool.recycleObject(filteredEvents.get(i));
			}
			filteredEvents.clear();
			
			//Frees buffered events.
			for(int i = 0; i < bufferedEvents.size(); i++){
				touchEventPool.recycleObject(bufferedEvents.get(i));
			}
			bufferedEvents.clear();
						
			//Moves the selected TouchEvents to the filtered list.
			filteredEvents.addAll(tempEvents);
			tempEvents.clear();
		}
	}
	
	/**
	 * Returns a reference to a filtered list of TouchEvents.
	 * It contains one TouchEvent per active pointer with the priority:
	 * TOUCH_UP > TOUCH_DOWN > TOUCH_DRAGGED
	 * @return list of TouchEvents
	 */
	@Override
	public List<TouchEvent> getTouchEvents() {
		synchronized(this){
			return filteredEvents;
		}
	}
	
	private TouchEvent getTouchEvent(int pointer, double x, double y, TouchType type){
		TouchEvent event = touchEventPool.getRecycledObject();
		event.set(pointer, x, y, type);
		
		return event;
	}
	
	/*
	 * Makes sure the location is inside the view.
	 * If the location is outside it changes it to the closest location inside the view.
	 */
	private double fixBoundsX(double x){
		double xInner = x;
		if(xInner < 0){
			xInner = 0;
		}else if(xInner > viewX * scalingFactor){
			xInner = viewX * scalingFactor;
		}
		return xInner;
	}
	
	/*
	 * Makes sure the location is inside the view.
	 * If the location is outside it changes it to the closest location inside the view.
	 */
	private double fixBoundsY(double y){
		double yInner = y;
		if(yInner < 0){
			yInner = 0;
		}else if(yInner > viewY * scalingFactor){
			yInner = viewY * scalingFactor;
		}
		return yInner;
	}

	@Override
	public double getScalingFactor() {
		return scalingFactor;
	}

	@Override
	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	@Override
	public void setScalingFactorTargetWidth(double targetWidth) {
		this.scalingFactor = targetWidth/view.getWidth();
	}

	@Override
	public void setScalingFactorTargetHeight(double targetHeight) {
		this.scalingFactor = targetHeight/view.getHeight();
	}
}
