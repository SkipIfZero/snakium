package com.skipifzero.snakium;

import android.graphics.Color;

public final class Const {
	private Const() {
		throw new AssertionError();
	}
	
	public static final String SKIPIFZERO_WEBSITE_URL = "http://www.skipifzero.com";
	public static final String COFFER_WEBSITE_URL = "http://www.coffer.se";
	
	//Colors
	public static final int BACKGROUND_COLOR = Color.argb(255, 50, 50, 50);
	
	public static final int FONT_COMPLETE_COLOR = Color.argb(255, 255, 255, 255);
	public static final int FONT_BLACK_COLOR = Color.argb(255, 0, 0, 0);
	
	
	public static final double BORDER_WIDTH_RATIO = 1.0/64.0;
}

