package com.skipifzero.snakium.framework.opengl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLUtils;

import com.skipifzero.snakium.framework.io.AssetsIO;

/**
 * An implementation of Texture that takes a path to an image in the assets folder as the argument.
 * @author Peter Hillerström
 * @version 1
 */
public class FileTexture implements Texture {

	private final AssetsIO file;
	private final String fileName;
	private final Bitmap.Config quality;

	private int id;
	private boolean smoothing;
	private int width, height;

	public FileTexture(AssetManager assets, String fileName, Bitmap.Config quality) {
		this.file = new AssetsIO(assets);
		this.fileName = fileName;
		this.quality = quality;

		load();
	}

	private void load() {
		//Gets id.
		int[] ids = new int[1];
		GLES10.glGenTextures(1, ids, 0);
		id = ids[0];

		Bitmap bitmap = file.loadBitmap(fileName, quality);

		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();

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
		} else {
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
			GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
		}
		unbind();
	}
	
	/**
	 * Reloads this texture.
	 */
	public void reload() {
		load();
		bind();
		setSmoothing(smoothing);
		unbind();
	}

	@Override
	public void dispose(){
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
		int[] IDs = {id};
		GLES10.glDeleteTextures(1, IDs, 0);
	}

	@Override
	public boolean isSmoothed() {
		return smoothing;
	}
	
	@Override
	public int getWidth(){
		return width;
	}

	@Override
	public int getHeight(){
		return height;
	}
}
