package com.skipifzero.snakium.challenges;

import java.util.List;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.Settings;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.screens.MConst;
import com.skipifzero.snakium.screens.SnakiumGLScreen;

public class DescriptionScreen extends SnakiumGLScreen {

	private static final double DESCRIPTION_Y_TOP = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING;
	private static final double DESCRIPTION_SIZE = MConst.TITLE_SIZE*0.8;
	private static final double DESCRIPTION_PADDING = 2;
	private static final double DESCRIPTION_WIDTH = MConst.MIN_CAM_WIDTH - DESCRIPTION_PADDING*2;
	
	private final String title, description;
	
	private final Camera2D cam;
	private final TouchButton backButton;
	
	public DescriptionScreen(SnakiumGLScreen snakiumGLScreen, String title, String description) {
		super(snakiumGLScreen);
		this.title = title;
		this.description = description;
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
	}
	
	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			changeGLScreen(new ChallengeScreen(this));
		}

	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.begin(Const.FONT_COMPLETE_COLOR);
		assets.font.draw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, title);
		assets.font.setHorizontalAlignment(HorizontalAlignment.LEFT);
		assets.font.setVerticalAlignment(VerticalAlignment.TOP);
		assets.font.drawRowBreaking(DESCRIPTION_PADDING, DESCRIPTION_Y_TOP, DESCRIPTION_SIZE, DESCRIPTION_WIDTH, description);
		assets.font.render();
	}

	@Override
	public void resume() {
		if(Settings.music() && assets.MENU_MUSIC != null) {
			assets.MENU_MUSIC.play();
		}
	}

	@Override
	public void pause() {
		if(assets.MENU_MUSIC != null && assets.MENU_MUSIC.isPlaying()) {
			assets.MENU_MUSIC.stop();
		}
	}

	@Override
	public boolean catchBackKey() {
		return true;
	}
}
