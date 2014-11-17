package com.skipifzero.snakium.screens;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLES10;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.FragmentGLActivity;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumActivity;
import com.skipifzero.snakium.Settings.ScreenOrientation;
import com.skipifzero.snakium.framework.DisplayScaling;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.input.PooledTouchInput;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.input.TouchInput;
import com.skipifzero.snakium.framework.opengl.GLScreen;

public abstract class SnakiumGLScreen implements GLScreen {
	
	protected final FragmentGLActivity fragmentGLActivity;
	protected final Assets assets;
	protected final DisplayScaling scaler;
	protected final TouchInput touchInput;
	private final NumberBuilder fpsBuilder;
	
	public SnakiumGLScreen(SnakiumGLScreen snakiumGLScreen) {
		this(snakiumGLScreen.fragmentGLActivity, snakiumGLScreen.assets);
	}
	
	public SnakiumGLScreen(FragmentGLActivity fragmentGLActivity, Assets assets) {
		this.fragmentGLActivity = fragmentGLActivity;
		fixScreenOrientation();
		
		this.assets = assets;
		this.scaler = new DisplayScaling(fragmentGLActivity.getGLSurfaceView(), fragmentGLActivity.getWindowManager());
		this.touchInput = new PooledTouchInput(fragmentGLActivity.getGLSurfaceView(), 5);
		touchInput.setScalingFactorTargetWidth(scaler.getViewWidthDps());
		this.fpsBuilder = new NumberBuilder("FPS: ", 0);
	}
	
	/*
	 * Inherited "game-loop" methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public final void update(double deltaTime, int fps) {
		touchInput.update();
		update(deltaTime, touchInput.getTouchEvents(), fragmentGLActivity.isBackPressed());
		draw(deltaTime, fps);
	}
	
	@Override
	public final void draw(double deltaTime, int fps) {
		//Set clear color and clear screen.
		GLES10.glClearColor(Color.red(Const.BACKGROUND_COLOR)/255f, Color.green(Const.BACKGROUND_COLOR)/255f, Color.blue(Const.BACKGROUND_COLOR)/255f, 1);
		GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT);

		//Enable alpha blending.
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

		//Enable textures
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		
		//Update fpsBuilder and call abstract draw() method.
		fpsBuilder.update(fps);
		draw(fpsBuilder);
	}

	/*
	 * Abstract methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public abstract void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed);
	
	public abstract void draw(NumberBuilder fpsBuilder);
	
	public abstract void resume();
	
	public abstract void pause();
	
	/*
	 * Inherited life-cycle methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public final void onResume() {
		assets.reload();
		resume();
	}

	@Override
	public final void onPause(PauseType pauseType) {
		switch(pauseType) {
			case NORMAL:
				assets.pause();
				//$FALL-THROUGH$
			case DONT_PAUSE_ASSETS:
				pause();
				break;
			default:
				throw new AssertionError();
		}
	}

	@Override
	public final void dispose() {
		assets.dispose();
	}
	
	/*
	 * Utils
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public final int getViewWidth() {
		return fragmentGLActivity.getViewWidth();
	}
	
	public final int getViewHeight() {
		return fragmentGLActivity.getViewHeight();
	}
	
	public final void changeGLScreen(SnakiumGLScreen glScreen) {
		fragmentGLActivity.changeGLScreen(glScreen);
	}
	
	public final void restartActivity() {
		Intent intent = fragmentGLActivity.getIntent();
		fragmentGLActivity.finish();
		SnakiumActivity.keepAssets(assets);
		fragmentGLActivity.startActivity(intent);
	}
	
	public final void fixScreenOrientation() {
		if(Settings.screenOrientation() != ScreenOrientation.LAYOUT) {
			switch(Settings.screenOrientation()) {
				case PORTRAIT:
					if(fragmentGLActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
						fragmentGLActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
					break;
				case LANDSCAPE:
					if(fragmentGLActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
						fragmentGLActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
					break;
				case PORTRAIT_REVERSE:
					if(fragmentGLActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
						fragmentGLActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					}
					break;
				case LANDSCAPE_REVERSE:
					if(fragmentGLActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						fragmentGLActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
					break;
				default:
					throw new AssertionError();
			}
		}
	}
	
	public final void openURL(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		fragmentGLActivity.startActivity(browserIntent);
	}
}
