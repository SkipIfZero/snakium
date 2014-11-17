package com.skipifzero.snakium.screens;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES10;

import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.NumberBuilder;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.TouchButton.TouchButtonType;
import com.skipifzero.snakium.framework.input.TouchEvent;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.viewmodels.CreditDescriptor;

public class AboutScreen extends SnakiumGLScreen {
	
	private static final double PADDING = 4;
	
	private static final String SCREEN_TITLE = "about";
	
	private final Camera2D cam;
	private final TouchButton backButton;
	
	private final List<CreditDescriptor> credits = new ArrayList<CreditDescriptor>();
	private final TouchButton skipifzeroLogoButton, cofferLogoButton;
	
	public AboutScreen(SnakiumGLScreen snakiumGLScreen) {
		super(snakiumGLScreen);
		
		this.cam = SnakiumUtils.createCameraWithYDiff(scaler, MConst.MIN_CAM_WIDTH, MConst.MIN_CAM_HEIGHT);
		touchInput.setScalingFactorTargetWidth(cam.getWidth());
		
		
		double totalSize = MConst.MIDDLE_PART_HEIGHT_MINUS_PADDING - PADDING*3;
		double creditHeight = totalSize/4 < MConst.MIN_CAM_WIDTH/4 ? totalSize/4 : MConst.MIN_CAM_WIDTH/4;
		double logoHeight = creditHeight;
		
		double yPos = MConst.MIN_CAM_HEIGHT - MConst.TOP_PART_HEIGHT - MConst.MIDDLE_TOP_PADDING - creditHeight/2;
		credits.add(new CreditDescriptor(cam.getX(), yPos, creditHeight, Const.BORDER_WIDTH_RATIO, "programming, design, graphics", "Peter Hillerström"));
		yPos = yPos - creditHeight/2 - PADDING - logoHeight/2;
		this.skipifzeroLogoButton = new TouchButton(cam.getX(), yPos, logoHeight*4, logoHeight, TouchButtonType.ACTIVATE_ON_RELEASE);
		yPos = yPos - logoHeight/2 - PADDING - creditHeight/2;
		credits.add(new CreditDescriptor(cam.getX(), yPos, creditHeight, Const.BORDER_WIDTH_RATIO, "musix & sfx design", "Coffer"));
		yPos = yPos - creditHeight/2 - PADDING - logoHeight/2;
		this.cofferLogoButton = new TouchButton(cam.getX(), yPos, logoHeight*4, logoHeight, TouchButtonType.ACTIVATE_ON_RELEASE);
		
		this.backButton = MConst.createLeftNavButton(cam.getX());
	}

	@Override
	public void update(double deltaTime, List<TouchEvent> touchEvents, boolean backPressed) {
		backButton.update(touchEvents);
		if(backButton.isActivated() || backPressed) {
			changeGLScreen(new MainMenuScreen(this));
		}
		
		skipifzeroLogoButton.update(touchEvents);
		if(skipifzeroLogoButton.isActivated()) {
			openURL(Const.SKIPIFZERO_WEBSITE_URL);
		}
		
		cofferLogoButton.update(touchEvents);
		if(cofferLogoButton.isActivated()) {
			openURL(Const.COFFER_WEBSITE_URL);
		}
	}

	@Override
	public void draw(NumberBuilder fpsBuilder) {
		cam.initialize(getViewWidth(), getViewHeight());
		
		for(CreditDescriptor creditDescriptor : credits) {
			creditDescriptor.render(assets);
		}
		
		//Skipifzero logo
		if(skipifzeroLogoButton.isTouched()) {
			GLES10.glColor4f(1, 1, 1, 0.75f);
		}
		assets.batcher.beginBatch(assets.texAtlas1024);
		assets.batcher.draw(skipifzeroLogoButton.getBounds(), assets.SKIPIFZERO_LOGO); 
		assets.batcher.renderBatch();
		GLES10.glColor4f(1, 1, 1, 1);
		
		//Coffer logo
		if(cofferLogoButton.isTouched()) {
			GLES10.glColor4f(1, 1, 1, 0.75f);
		}
		assets.batcher.beginBatch(assets.texAtlas1024);
		assets.batcher.draw(cofferLogoButton.getBounds(), assets.COFFER_LOGO); 
		assets.batcher.renderBatch();
		GLES10.glColor4f(1, 1, 1, 1);
		
		SnakiumUtils.renderTouchButtonComplete(backButton, "back", MConst.NAV_BUTTON_TEXT_SCALING_FACTOR, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		assets.font.completeDraw(cam.getX(), MConst.TITLE_Y_CENTER_POS, MConst.TITLE_SIZE, Const.FONT_COMPLETE_COLOR, SCREEN_TITLE);
	}

	@Override
	public void resume() {
		//Do nothing.
	}

	@Override
	public void pause() {
		//Do nothing.
	}
	
	@Override
	public boolean catchBackKey() {
		return true;
	}
}
