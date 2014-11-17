package com.skipifzero.snakium.framework.input;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * A class used for checking if the back button is pressed or not.
 * 
 * @author Peter Hillerström
 * @since 2013-07-08
 * @version 2
 */

public class BackKeyInput implements OnKeyListener {

	/*
	 * Honestly, I'm really not sure if the synchronization is correct. I haven't had any problems
	 * during testing, so I'm going to assume that it is working. I can't imagine something
	 * particularly bad happening anyway. A false positive isn't really possible, but a false
	 * negative might be.
	 */
	
	
	private static final int BACK_KEY_CODE = 4;
	private boolean catchBackKey;
	
	private volatile boolean backPressed = false;
	private volatile boolean backAlreadyPressed = false;
	
	public BackKeyInput(View view, boolean catchBackKey) {
		view.setOnKeyListener(this);
		this.catchBackKey = catchBackKey;
		
		//Gives view focus so it can receive key events.
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		if(keyCode == BACK_KEY_CODE) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				//synchronized(this) {
					backPressed = true;
				//}
				return catchBackKey;
			}
			else if(event.getAction() == KeyEvent.ACTION_UP) {
				//synchronized(this) {
					backPressed = false;
					backAlreadyPressed = false;
				//}
				return catchBackKey;
			}
		}
		
		return false;
	}
	
	public boolean isBackPressed() {
		//synchronized(this) {
			boolean tempBackAlreadyPressed = backAlreadyPressed;
			if(backPressed) {
				backAlreadyPressed = true;
			}
		//}
		return backPressed && !tempBackAlreadyPressed;
	}
	
	public void catchBackKey(boolean catchBackKey) {
		this.catchBackKey = catchBackKey;
	}
}
