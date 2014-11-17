package com.skipifzero.snakium.screens;

import java.util.List;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.FragmentGLActivity;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.input.TouchEvent;

/**
 * Dummy implementation of SnakiumGLScreen. Only exists because I'm too lazy to write constructors
 * with "(GLActivity glActivity, Assets assets)" for every (or one) Screen.
 * @author Peter Hillerström
 */
public final class DummyScreen extends SnakiumGLScreen {

	public DummyScreen(FragmentGLActivity fragmentGLActivity, Assets assets) {
		super(fragmentGLActivity, assets);
	}
	
	@Override
	public boolean catchBackKey() {
		return false;
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		//Do nothing.
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		//Do nothing.
	}

	@Override
	public void resume() {
		//Do nothing.
	}

	@Override
	public void pause() {
		//Do nothing.
	}
}
