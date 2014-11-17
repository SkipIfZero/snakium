package com.skipifzero.snakium;

import com.skipifzero.snakium.framework.opengl.GLScreen;
import com.skipifzero.snakium.screens.DummyScreen;
import com.skipifzero.snakium.screens.MainMenuScreen;

public class SnakiumActivity extends FragmentGLActivity {
	
	private static Assets assets = null;
	
	@Override
	public GLScreen getInitialGLScreen(FragmentGLActivity fragmentGLActivity) {
		if(assets == null) {
			assets = new Assets(getAssets());
		}
		Assets tempAssets = assets;
		assets = null;
		
		return new MainMenuScreen(new DummyScreen(fragmentGLActivity, tempAssets));
	}

	@Override
	public boolean enableFullscreenMode() {
		Settings.load();
		return Settings.fullscreen();
	}
	
	public static void keepAssets(Assets assets) {
		SnakiumActivity.assets = assets;
	}
}
