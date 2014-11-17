package com.skipifzero.snakium;

import java.util.List;

import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.math.Vector2;
import com.skipifzero.snakium.model.SnakeDirection;

public class TouchController {
	
	private static final double THRESHOLD = 3;
	
	private SnakeDirection direction = SnakeDirection.NONE;
	
	private final Vector2 startPos = new Vector2(-1,-1);
	private final Vector2 latestPos = new Vector2(-1,-1);
	
	private boolean vectorsSet = false;
	private boolean active = false;
	private final Vector2 temp = new Vector2(-1,-1);
	
	public SnakeDirection getSnakeDirection(List<TouchEvent> touchEvents) {
		
		if(touchEvents.size() > 0) {
			
			TouchEvent currentEvent = touchEvents.get(0);
			
			switch(currentEvent.getType()) {
				case TOUCH_DOWN:
					currentEvent.getPosition(startPos);
					latestPos.set(-1, -1);
					break;
					
				case TOUCH_DRAGGED:
					currentEvent.getPosition(latestPos);
					active = true;
					break;
				
				case TOUCH_UP:
					currentEvent.getPosition(latestPos);
					active = false;
					break;
					
				case NOT_TOUCHING:
				default:
					//Do nothing.
			}
			
			if(startPos.getX() != -1 && startPos.getY() != -1 && latestPos.getX() != -1 && latestPos.getY() != -1 
					&& temp.set(latestPos).sub(startPos).getLength() >= THRESHOLD) {
				vectorsSet = true;
			} else {
				vectorsSet = false;
			}
		}
		
		if(vectorsSet) {
			double angle = temp.set(latestPos).sub(startPos).getAngle();
			
			if(angle <= 45 || angle > 315) {
				direction = SnakeDirection.RIGHT;
			} else if(angle <= 135 && angle > 45) {
				direction = SnakeDirection.UP;
			} else if(angle <= 225 && angle > 135) {
				direction = SnakeDirection.LEFT;
			} else if(angle <= 315 && angle > 225) {
				direction = SnakeDirection.DOWN;
			}
		}
		
		return direction;
	}
	
	public Vector2 getStartPosition() {
		return startPos;
	}
	
	public Vector2 getCurrentPosition() {
		return latestPos;
	}
	
	public boolean isActive() {
		return active && vectorsSet;
	}
}
