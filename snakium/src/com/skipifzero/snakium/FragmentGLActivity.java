package com.skipifzero.snakium;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.skipifzero.snakium.framework.input.BackKeyInput;
import com.skipifzero.snakium.framework.opengl.GLScreen;
import com.skipifzero.snakium.framework.opengl.GLScreen.PauseType;

/**
 * Same as version 4 of normal GLActivity, only difference is that this extends FragmentActivity
 * instead of Activity.
 */
public abstract class FragmentGLActivity extends FragmentActivity implements Renderer {
	
	//States used to make sure everything in the GLController happens on the rendering thread.
	private enum State {
		STARTING, RUNNING, PAUSING, FINISHING, SLEEPING;
	}
	
	private GLSurfaceView glSurfaceView;
	private GLScreen glScreen;
	
	private volatile State state;
	
	//Variables for calculating and storing deltatime and fps.
	private long startTime = 0;
	private long lastFPSCount = 0;
	private int frameCount = 0;
	private int fps = 0;
	private double deltaTime = 0;
	private StringBuilder fpsBuilder = new StringBuilder("FPS: ");
	
	private BackKeyInput keyInput;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.state = State.STARTING;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes title bar.
		if(enableFullscreenMode()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		//Sets VolumeControlStream to STREAM_MUSIC
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setRenderer(this);
		
		setContentView(glSurfaceView);
		
		keyInput = new BackKeyInput(glSurfaceView, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		glSurfaceView.onResume();
	}
	
	@Override
	public void onPause() {
		synchronized(this) {
			if(isFinishing()) {
				state = State.FINISHING;
			} else {
				state = State.PAUSING;
			}
			
			//Wait for rendering thread to finish cleaning up.
			try {
				this.wait();
			} catch(InterruptedException e) {
				//Do nothing.
			}
		}
		glSurfaceView.onPause();
		super.onPause();
	}
	
	/*
	 * OpenGL Renderer methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		//Resets fps counter.
		startTime = System.nanoTime();
		lastFPSCount = startTime;
		frameCount = 0;
		fps = 0;
		deltaTime = 0;
		
		synchronized(this) {
			if(state == State.STARTING) { //Gets initial GLController if program is starting up.
				glScreen = getInitialGLScreen(this);
			}
			state = State.RUNNING; //Surface was created, so program is running.
			glScreen.onResume();
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		//Do nothing.
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		//Stores state in local variable to avoid synchronizing bugs.
		State localState = null;
		synchronized(this) {
			localState = this.state;
		}
		
		switch(localState) {
			case RUNNING:
				//Calculates delta.
				deltaTime = (System.nanoTime()-startTime) / 1000000000.0;
				startTime = System.nanoTime();
				
				//Calculates current fps.
				frameCount++;
				if(startTime - lastFPSCount >= 1000000000.0){
					fps = frameCount;
					fpsBuilder.delete(5, fpsBuilder.length());
					fpsBuilder.append(fps);
					if(fps < 30) { //TODO: To be removed. Only log fps if it falls below 30.
						Log.d("FPS", fpsBuilder.toString());
					}
					frameCount = 0;
					lastFPSCount = System.nanoTime();
				}
				
				//Updates current GLController and GLView
				glScreen.update(deltaTime, fps);
				break;
			
			case PAUSING:
				glScreen.onPause(PauseType.NORMAL);
				synchronized(this) {
					this.state = State.SLEEPING;
					this.notifyAll();
				}
				break;
			
			case FINISHING:
				glScreen.onPause(PauseType.NORMAL);
				glScreen.dispose();
				synchronized(this) {
					this.state = State.SLEEPING;
					this.notifyAll();
				}
				break;
			
			case STARTING:
			case SLEEPING:
			default:
				throw new AssertionError("Illegal state in GLActivity");
		}
	}
	
	/*
	 * Abstract methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * This method should return the initial GLScreen, it may not return null. Is called on the
	 * rendering thread in the onSurfaceCreated methods.
	 * Probably not the standard way of doing things, but don't really have a choice due to how
	 * Android activities work.
	 * @return initial GLScreen
	 */
	public abstract GLScreen getInitialGLScreen(FragmentGLActivity fragmentGLActivity);
	
	/**
	 * This method returns whether fullscreen should be enabled or not.
	 * @return whether fullscreen should be enabled or not
	 */
	public abstract boolean enableFullscreenMode();
	
	/*
	 * Public Methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Changes active GLScreen.
	 * Will not call dispose() in old GLScreen and will send "DONT_PAUSE_ASSETS" to onPause().
	 * Can for example be used if some kind of shared Assets system is used. Can be called from 
	 * anywhere within a GLScreen.
	 * @throws IllegalArgumentException if new GLScreen is null
	 * @param glScreen the new GLScreen
	 */
	public void changeGLScreen(GLScreen glScreen) {
		if(glScreen == null) {
			throw new IllegalArgumentException("New GLScreen is null, not allowed.");
		}
		
		//Limited pause of old GLController
		this.glScreen.onPause(PauseType.DONT_PAUSE_ASSETS);
		
		//Resumes new GLController
		glScreen.onResume();
		this.glScreen = glScreen;
		
		catchBackKey(glScreen.catchBackKey());
	}
	
	/**
	 * Same as changeGLScreen() with the exception that it will give "NORMAL" as an argument to the
	 * old GLScreen's onPause() and call dispose(). Should be used if no shared Assets system is
	 * used and each screen has its own assets.
	 * @throws IllegalArgumentException if new GLScreen is null
	 * @param glScreen the new GLScreen
	 */
	public void changeGLScreenDispose(GLScreen glScreen) {
		if(glScreen == null) {
			throw new IllegalArgumentException("New GLController is null, not allowed.");
		}
		
		//Dispose of old GLController
		this.glScreen.onPause(PauseType.NORMAL);
		this.glScreen.dispose();
		
		//Resumes new GLController
		glScreen.onResume();
		this.glScreen = glScreen;
		
		catchBackKey(glScreen.catchBackKey());
	}
	
	/**
	 * Returns the current active GLController.
	 * @return current active GLController
	 */
	public GLScreen getCurrentGLController() {
		return glScreen;
	}
	
	/**
	 * Returns the width of the GLSurfaceView in pixels.
	 * @return width
	 */
	public int getViewWidth() {
		return glSurfaceView.getWidth();
	}

	/**
	 * Returns the height of the GLSurfaceView in pixels.
	 * @return height
	 */
	public int getViewHeight() {
		return glSurfaceView.getHeight();
	}

	/**
	 * Returns a reference to the GLSurfaceView.
	 * @return glSurfaceView
	 */
	public GLSurfaceView getGLSurfaceView() {
		synchronized(this) {
			return glSurfaceView;
		}
	}
	
	/**
	 * Returns true if back button is pressed, false otherwise.
	 * @return whether back is pressed or not
	 */
	public boolean isBackPressed() {
		return keyInput.isBackPressed();
	}
	
	/**
	 * Sets if back button presses should be caught or not. If they are caught they will do nothing,
	 * if they aren't caught they will exit the Activity.
	 * @param catchBackKey
	 */
	public void catchBackKey(boolean catchBackKey) {
		keyInput.catchBackKey(catchBackKey);
	}
}
