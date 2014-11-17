package com.skipifzero.snakium.screens;

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
import com.skipifzero.snakium.model.SnakiumModel;
import com.skipifzero.snakium.viewmodels.SnakiumConfigDescriptor;

public class ModelInfoScreen extends SnakiumGLScreen {

	private static final double MIDDLE_X_PADDING = 3;
	
	private static final double DESCRIPTION_SIZE = 5.5;
	private static final double DESCRIPTION_WIDTH = MConst.MIN_CAM_WIDTH - 2*MIDDLE_X_PADDING;
	
	private final SnakiumModel.SnakiumConfig config;
	private final String configTitle, configDescription;
	
	private final Camera2D cam;
	
	private final SnakiumConfigDescriptor configDescr;
	private final TouchButton backButton, playButton;
	
	public ModelInfoScreen(SnakiumGLScreen snakiumGLScreen, SnakiumModel.SnakiumConfig config, String configTitle, String configDescription) {
		super(snakiumGLScreen);
		this.config = config;
		this.configTitle = configTitle;
		this.configDescription = configDescription;
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
		this.playButton = MConst.createRightNavButton(cam.getX());
		
		//double descrPart = DESCRIPTION_SIZE*2 + MConst.MIDDLE_TOP_PADDING;
		double configDescrSize = DESCRIPTION_WIDTH;
		//double configDescrY = MConst.BOTTOM_PART_HEIGHT + (MConst.MIDDLE_PART_HEIGHT - descrPart)/2;
		double configDescrY = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_PART_HEIGHT/2;
		this.configDescr = new SnakiumConfigDescriptor(cam.getX(), configDescrY, configDescrSize, Const.BORDER_WIDTH_RATIO, config);
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		if(backPressed) {
			changeGLScreen(new ModeSelectScreen(this));
		}
		
		backButton.update(touchEvents);
		playButton.update(touchEvents);
		
		if(backButton.isActivated()) {
			changeGLScreen(new ModeSelectScreen(this));
		}
		
		else if(playButton.isActivated()) {
			changeGLScreen(new GameScreen(this, new SnakiumModel(config), backButton.isTouched()));
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		configDescr.render(assets);
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		SnakiumUtils.renderTouchButtonComplete(playButton, "play", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.completeDraw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, Const.FONT_COMPLETE_COLOR, configTitle);
//		assets.font.setHorizontalAlignment(HorizontalAlignment.LEFT);
//		assets.font.setVerticalAlignment(VerticalAlignment.TOP);
//		assets.font.begin(Const.FONT_COMPLETE_COLOR);
//		assets.font.drawRowBreaking(cam.getX() - (DESCRIPTION_WIDTH)/2, MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT, DESCRIPTION_SIZE, DESCRIPTION_WIDTH, configDescription); //Left aligned
//		assets.font.render();
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
