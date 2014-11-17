package com.skipifzero.snakium.screens;

import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;


public final class MConst {
	
	//No instantiation.
	private MConst() {
		throw new AssertionError();
	}
	
	//Camera
	public static final double MIN_CAM_WIDTH = 100;
	public static final double MIN_CAM_HEIGHT = 160;
	
	
	//Top part
	public static final double TOP_TOP_PADDING = 2;
	public static final double TOP_CONTENT_HEIGHT = 14;
	public static final double TOP_BOTTOM_PADDING = 4;
	public static final double TOP_PART_HEIGHT = TOP_TOP_PADDING + TOP_CONTENT_HEIGHT + TOP_BOTTOM_PADDING;
	
	//Title
	public static final double TITLE_SIZE = TOP_CONTENT_HEIGHT;
	public static final double TITLE_Y_CENTER_POS = MIN_CAM_HEIGHT - TOP_TOP_PADDING - TOP_CONTENT_HEIGHT/2;
	
	
	//Bottom part
	public static final double BOTTOM_TOP_PADDING = 4;
	public static final double BOTTOM_CONTENT_HEIGHT = 13;
	public static final double BOTTOM_BOTTOM_PADDING = 3;
	public static final double BOTTOM_PART_HEIGHT = BOTTOM_TOP_PADDING + BOTTOM_CONTENT_HEIGHT + BOTTOM_BOTTOM_PADDING;
	
	//Navigation buttons
	public static final double NAV_BUTTON_HEIGHT = BOTTOM_CONTENT_HEIGHT;
	public static final double NAV_BUTTON_WIDTH = 32;
	public static final double NAV_BUTTON_X_PADDING = 8;
	public static final double NAV_BUTTON_TEXT_SCALING_FACTOR = 0.5;
	public static final double NAV_BUTTON_Y_CENTER_POS = BOTTOM_BOTTOM_PADDING + NAV_BUTTON_HEIGHT/2;
	
	
	//Middle
	public static final double MIDDLE_TOP_PADDING = 1;
	public static final double MIDDLE_BOTTOM_PADDING = 1;
	public static final double MIDDLE_PART_HEIGHT = MIN_CAM_HEIGHT - TOP_PART_HEIGHT - BOTTOM_PART_HEIGHT;
	public static final double MIDDLE_PART_HEIGHT_MINUS_PADDING = MIDDLE_PART_HEIGHT - MIDDLE_TOP_PADDING - MIDDLE_BOTTOM_PADDING;
	
	//Menu Button
	public static final double MENU_BUTTON_PADDING = 6;
	public static final double MENU_BUTTON_WIDTH = 48;
	public static final double MENU_BUTTON_TEXT_SCALING_FACTOR = 0.55;
	
	
	public static TouchButton createLeftNavButton(double camX) {
		return new TouchButton(camX - (NAV_BUTTON_WIDTH/2) - NAV_BUTTON_X_PADDING, NAV_BUTTON_Y_CENTER_POS, NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
	}
	
	public static TouchButton createRightNavButton(double camX) {
		return new TouchButton(camX + (NAV_BUTTON_WIDTH/2) + NAV_BUTTON_X_PADDING, NAV_BUTTON_Y_CENTER_POS, NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT, TouchButtonType.ACTIVATE_ON_RELEASE);
	}
}
