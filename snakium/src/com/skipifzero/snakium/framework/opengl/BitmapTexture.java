package com.skipifzero.snakium.framework.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLUtils;

/**
 * An implementation of Texture that takes an Bitmap as the argument. Doesn't keep the bitmap
 * so it can't be reloaded.
 * @author Peter Hillerström
 * @version 1
 */
public class BitmapTexture implements Texture {

	private int id;
	private boolean smoothing;
	private final int width, height;
	
	public BitmapTexture(Bitmap bitmap) {		
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		
		load(bitmap);
	}
	
	private void load(Bitmap bitmap) {
		//Gets id.
		int[] ids = new int[1];
		GLES10.glGenTextures(1, ids, 0);
		id = ids[0];
		
		bind();
		GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
		setSmoothing(false);
		unbind();
	}
	
	@Override
	public void bind() {
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, id);
	}
	
	@Override
	public void unbind() {
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
	}
	
	@Override
	public void setSmoothing(boolean smoothing) {
		this.smoothing = smoothing;

		bind();
		if(smoothing) {
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
		}else {
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
		}
		unbind();
	}
	
	@Override
	public void dispose() {
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
		int[] IDs = {id};
		GLES10.glDeleteTextures(1, IDs, 0);
	}
	
	@Override
	public boolean isSmoothed() {
		return smoothing;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
}
