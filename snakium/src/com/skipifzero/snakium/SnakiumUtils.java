package com.skipifzero.snakium;

import android.util.Log;

import com.skipifzero.snakium.framework.DisplayScaling;
import com.skipifzero.snakium.framework.TouchButton;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.math.Vector2;
import com.skipifzero.snakium.framework.opengl.Camera2D;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.framework.opengl.TextureRegion;

public final class SnakiumUtils {
	
	private static final Vector2 tempVec = new Vector2(0,0);
	
	/**
	 * Class should not be instantiated.
	 */
	private SnakiumUtils() {
		throw new AssertionError();
	}
	
	public static void renderBorderComplete(BoundingRectangle bounds, double borderWidth, Assets assets) {
		assets.batcher.beginBatch(assets.texAtlas128);
		renderBorder(bounds, borderWidth, assets);
		assets.batcher.renderBatch();
	}
	
	public static void renderBorder(BoundingRectangle bounds, double borderWidth, Assets assets) {
		assets.batcher.draw(bounds.getX(), bounds.getY() + bounds.getHeight()/2 - borderWidth/2, bounds.getWidth(), borderWidth, assets.FILLED); //Top
		assets.batcher.draw(bounds.getX(), bounds.getY() - bounds.getHeight()/2 + borderWidth/2, bounds.getWidth(), borderWidth, assets.FILLED); //Bottom
		assets.batcher.draw(bounds.getX() - bounds.getWidth()/2 + borderWidth/2, bounds.getY(), borderWidth, bounds.getHeight() - 2*borderWidth, assets.FILLED); //Left
		assets.batcher.draw(bounds.getX() + bounds.getWidth()/2 - borderWidth/2, bounds.getY(), borderWidth, bounds.getHeight() - 2*borderWidth, assets.FILLED); //Right
	}
	
	/**
	 * Renders the specified TouchButton with the specified SpriteBatcher. The batcher must be
	 * "running". Makes 2 or 3 draw calls depending on state of the TouchButton.
	 * @param button the specified TouchButton
	 * @param batcher the "running" SpriteBatcher
	 * @param assets the Assets
	 */
	public static void renderTouchButton(TouchButton button, Assets assets) {
		TextureRegion leftRegion = assets.BUTTON_LEFT;
		TextureRegion middleRegion = null;
		TextureRegion rightRegion = assets.BUTTON_RIGHT;
		
		if(!button.isEnabled()) {
			leftRegion = assets.BUTTON_LEFT_DISABLED;
			rightRegion = assets.BUTTON_RIGHT_DISABLED;
		} else if(button.isTouched()) {
			leftRegion = assets.BUTTON_LEFT_TOUCHED;
			rightRegion = assets.BUTTON_RIGHT_TOUCHED;
			middleRegion = assets.BUTTON_MIDDLE_TOUCHED;
		}

		tempVec.set(button.getPosition()).sub(button.getWidth()/2, 0);
		assets.batcher.draw(tempVec, button.getHeight(), button.getHeight(), leftRegion);
		if(middleRegion != null) {
			assets.batcher.draw(button.getPosition(), button.getWidth() - button.getHeight(), button.getHeight(), middleRegion);
		}
		tempVec.set(button.getPosition()).add(button.getWidth()/2, 0);
		assets.batcher.draw(tempVec, button.getHeight(), button.getHeight(), rightRegion);
	}
	
	public static void renderTouchButtonComplete(TouchButton button, String buttonText, double textScalingFactor, Assets assets) {
		assets.batcher.beginBatch(assets.texAtlas128);
		renderTouchButton(button, assets);
		assets.batcher.renderBatch();
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		int color = Const.FONT_COMPLETE_COLOR;
		if(button.isTouched()) {
			color = Const.FONT_BLACK_COLOR;
		}
		assets.font.completeDraw(button.getPosition(), button.getHeight()*textScalingFactor, color, buttonText);
	}
	
	public static void renderTouchButtonComplete(TouchButton button, String buttonText, double textScalingFactor, Assets assets, boolean isTouched) {
		assets.batcher.beginBatch(assets.texAtlas128);
		
		TextureRegion leftRegion = assets.BUTTON_LEFT;
		TextureRegion middleRegion = null;
		TextureRegion rightRegion = assets.BUTTON_RIGHT;
		
		if(!button.isEnabled()) {
			leftRegion = assets.BUTTON_LEFT_DISABLED;
			rightRegion = assets.BUTTON_RIGHT_DISABLED;
		} else if(button.isTouched() || isTouched) {
			leftRegion = assets.BUTTON_LEFT_TOUCHED;
			rightRegion = assets.BUTTON_RIGHT_TOUCHED;
			middleRegion = assets.BUTTON_MIDDLE_TOUCHED;
		}
		
		tempVec.set(button.getPosition()).sub(button.getWidth()/2, 0);
		assets.batcher.draw(tempVec, button.getHeight(), button.getHeight(), leftRegion);
		if(middleRegion != null) {
			assets.batcher.draw(button.getPosition(), button.getWidth() - button.getHeight(), button.getHeight(), middleRegion);
		}
		tempVec.set(button.getPosition()).add(button.getWidth()/2, 0);
		assets.batcher.draw(tempVec, button.getHeight(), button.getHeight(), rightRegion);
		
		assets.batcher.renderBatch();
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.setVerticalAlignment(VerticalAlignment.CENTER);
		int color = Const.FONT_COMPLETE_COLOR;
		if(button.isTouched() || isTouched) {
			color = Const.FONT_BLACK_COLOR;
		}
		assets.font.completeDraw(button.getPosition(), button.getHeight()*textScalingFactor, color, buttonText);
	}
	
	/**
	 * Creates a 2D OpenGL camera with the same aspect ratio as the view specified by the specified
	 * DisplayScaling instance. It tries to make camera as close as possible but larger than the
	 * specified minimum width and height.
	 * 
	 * This version displaces the camera in the y-axis with (camHeight - minHeight)/2. This ensures
	 * that as long as you place everything relative to the middle of the screen in x-axis (not 
	 * y-axis) and within the minimum width and height everything will appear in the middle of the
	 * view.
	 * 
	 * @param scaler the DisplayScaling instance
	 * @param minWidth the minimum width of the 2D Camera
	 * @param minHeight the minimum height of the 2D Camera
	 * @return 
	 */
	@Deprecated
	public static Camera2D createCameraWithYDiff(DisplayScaling scaler, double minWidth, double minHeight) {
		double viewWidth = scaler.getViewWidthPixels();
		double viewHeight = scaler.getViewHeightPixels();
		double camWidth = minWidth;
		double camHeight = camWidth*(viewHeight/viewWidth);
		if(camHeight < minHeight) {
			camHeight = minHeight;
			camWidth = camHeight*(viewWidth/viewHeight);
		}
		double yDiff = 0.0;//(camHeight - minHeight)/2.0; //TODO: AWFUL, AWFUL hack fixing bad bad bug.
		Log.d("Camera2D", "Camera size: " + camWidth + " x " + camHeight + " yDiff: " + yDiff);
		return new Camera2D(camWidth/2, (camHeight/2) - yDiff, camWidth, camHeight);
	}
	
	/**
	 * Creates a 2D OpenGL camera with the same aspect ratio as the view specified by the specified
	 * DisplayScaling instance. It tries to make camera as close as possible but larger than the
	 * specified minimum width and height.
	 * 
	 * @param scaler the DisplayScaling instance
	 * @param minWidth the minimum width of the 2D Camera
	 * @param minHeight the minimum height of the 2D Camera
	 * @return
	 */
	public static Camera2D createCamera(DisplayScaling scaler, double minWidth, double minHeight) {
		double viewWidth = scaler.getViewWidthPixels();
		double viewHeight = scaler.getViewHeightPixels();
		double camWidth = minWidth;
		double camHeight = camWidth*(viewHeight/viewWidth);
		if(camHeight < minHeight) {
			camHeight = minHeight;
			camWidth = camHeight*(viewWidth/viewHeight);
		}
		Log.d("Camera2D", "Camera size: " + camWidth + " x " + camHeight);
		return new Camera2D(camWidth/2, camHeight/2, camWidth, camHeight);
	}
}
