package com.skipifzero.snakium.framework.opengl;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.skipifzero.snakium.framework.io.AssetsIO;

/**
 * A class used for loading textures from a directory and generating a texture atlas and corresponding
 * TextureRegions. 
 * 
 * In the specified directory there must only be image files, otherwise this class will probably crash when loading.
 * 
 * The algorithm for generating texture atlases is currently pretty inefficient. It will check the size of the
 * largest texture and then create "cells" which it will place each texture in. So to use this class most efficiently
 * all the textures in the specified folder should be of the same size and with a width/height equal to a power of two.
 * 
 * @author Peter Hillerström
 * @since 2013-04-21
 * @version 4
 */
public final class TextureUtil {
	private static final int MAX_TEXTURE_SIZE = 8192;
	private static final float TEXTURE_REGION_DELTA = 0.05f;//0.375f;
	
	private final String textureDirectory;
	private final Bitmap.Config quality;
	
	private Texture texture;
	private TextureRegion textureAtlasRegion;
	private Map<String, TextureRegion> textureRegions = new HashMap<String, TextureRegion>();

	/**
	 * Creates a new TextureUtil with the specified directory in the assets folder from which this will
	 * load textures. You must call load before this TextureUtil can be used.
	 * @param textureDirectory the directory to read textures from
	 * @param quality the quality of the generated texture atlas
	 */
	public TextureUtil(String textureDirectory, Bitmap.Config quality) {
		this.textureDirectory = textureDirectory;
		this.quality = quality;
	}
	
	/**
	 * Loads all the textures from this TextureUtil's folder and creates a new texture atlas and new TextureRegions.
	 * If you've previously called this method you should first call dispose before calling this method. The generated
	 * TextureRegions should be the same as the last time this was loaded.
	 * @return this TextureUtil
	 */
	public TextureUtil load(AssetManager assets) {
		List<String> textureRegionStrings = loadFileNames(assets);
		
		List<Bitmap> bitmaps = loadBitmaps(assets, this.textureDirectory, textureRegionStrings, quality);	
		int largestBitmapSize = getLargestBitmapSize(bitmaps);
		int textureSize = getTextureSize(largestBitmapSize, bitmaps.size());
				
		//Create bitmap atlas and drawing tools.
		Bitmap bitmapAtlas = Bitmap.createBitmap(textureSize, textureSize, quality);
		Log.d("TextureUtil", "Created new Bitmap with size: " + textureSize + "x" + textureSize);
		Canvas canvas = new Canvas(bitmapAtlas);
		Paint paint = new Paint();
		
		//Arrays with values for creation of TextureRegions.
		float[] xLeftArray = new float[bitmaps.size()];
		float[] yTopArray = new float[bitmaps.size()];
		
		//Temp variables 
		float xLeft = 0;
		float yTop = 0;
		
		//Draws all textures to the texture atlas.
		for(int i = 0; i < bitmaps.size(); i++) {
			canvas.drawBitmap(bitmaps.get(i), xLeft, yTop, paint);
			
			//Stores drawn location for creation of TextureRegions.
			xLeftArray[i] = xLeft;
			yTopArray[i] = yTop;
			
			//Updates location to draw next texture.
			xLeft += largestBitmapSize;
			if(xLeft + largestBitmapSize > textureSize) {
				yTop += largestBitmapSize;
				xLeft = 0;
			}
		}
		
		//Creates OpenGL texture from bitmap texture atlas.
		texture = new BitmapTexture(bitmapAtlas);
		textureAtlasRegion = new TextureRegion(texture, 0, 0, textureSize, textureSize);
		
		bitmapAtlas.recycle(); //Recycles bitmap texture atlas.
		
		//Creates TextureRegions for each individual texture on the texture atlas.
		//textureRegions = new TextureRegion[bitmaps.size()];
		TextureRegion temp;
		for(int i = 0; i < bitmaps.size(); i++) {
			temp = new TextureRegion(texture, xLeftArray[i] + TEXTURE_REGION_DELTA, yTopArray[i] + TEXTURE_REGION_DELTA, bitmaps.get(i).getWidth() - 2*TEXTURE_REGION_DELTA, bitmaps.get(i).getHeight() - 2*TEXTURE_REGION_DELTA);
		
			textureRegions.put(textureRegionStrings.get(i), temp);
			
			bitmaps.get(i).recycle(); //Recycles bitmap texture.
		}
		return this;
	}
	
	/**
	 * Disposes of the internal texture atlas.
	 * Warning, since TextureUtil gives away direct references to its internal texture atlas this method
	 * will also dispose of textures outside this class that originated in this class.
	 */
	public void dispose() {
		texture.dispose();
	}
	
	/**
	 * Returns this TextureUtils directory path (relative to asset folder).
	 * @return this TextureUtils directory path
	 */
	public String getTextureDirectory() {
		return textureDirectory;
	}
	
	/**
	 * Returns a String collection with all the filenames of the files in the texture directory.
	 * @return texture names
	 */
	public Collection<String> getTextureNames() {
		return textureRegions.keySet();
	}
	
	/**
	 * Returns the texture atlas.
	 * @return texture atlas
	 */
	public Texture getTextureAtlas() {
		return texture;
	}
	
	/**
	 * Returns a TextureRegion containing the whole texture atlas.
	 * @return TextureRegion containing the whole texture atlas
	 */
	public TextureRegion getTextureAtlasRegion() {
		return textureAtlasRegion;
	}
	
	/**
	 * Returns a TextureRegion containing the specified texture.
	 * @param textureFileName the file name of the {@link Texture}
	 * @throws IllegalArgumentException if texture doesn't exist
	 * @return TextureRegion containing the specified texture
	 */
	public TextureRegion getTextureRegion(String textureFileName) {
		TextureRegion textureRegion = textureRegions.get(textureFileName);
		if(textureRegion == null) {
			throw new IllegalArgumentException(textureFileName + " doesn't exist in selected TextureUtil.");
		}
		return textureRegion;
	}
	
	/**
	 * Returns a TextureRegion collection containing all the textures in this TextureUtil's directory.
	 * @return TextureRegion collection containing all the textures in this TextureUtil's directory
	 */
	public Collection<TextureRegion> getTextureRegions() {
		return textureRegions.values();
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private static int getTextureSize(int largestBitmapSize, int amountOfBitmaps) {
		int textureSize = 128;
		while(true) {
			int cellsPerRowOrCol = textureSize / largestBitmapSize;
			
			if(cellsPerRowOrCol*cellsPerRowOrCol >= amountOfBitmaps) {
				break;
			}
			
			textureSize *= 2;
		}
		if(textureSize > MAX_TEXTURE_SIZE) {
			throw new IllegalArgumentException("Couldn't fit textures on atlas, size needed " + textureSize + ", max size " + MAX_TEXTURE_SIZE + ".");
		}
		return textureSize;
	}
	
	private static int getLargestBitmapSize(List<Bitmap> bitmaps) {
		int width = 0;
		int height = 0;
		for(Bitmap bitmap : bitmaps) {
			if(bitmap.getWidth() > width) {
				width = bitmap.getWidth();
			}
			if(bitmap.getHeight() > height) {
				height = bitmap.getHeight();
			}
		}
		return width > height ? width : height;
	}
	
	private static List<Bitmap> loadBitmaps(AssetManager assets, String directory, Collection<String> fileNames, Bitmap.Config config) {
		AssetsIO file = new AssetsIO(assets);
		List<Bitmap> bitmaps = new ArrayList<Bitmap>(fileNames.size());
		for(String fileName : fileNames) {
			bitmaps.add(file.loadBitmap(directory + File.separator + fileName, config));
		}
		return bitmaps;
	}
	
	private List<String> loadFileNames(AssetManager assets) {
		try {
			List<String> files = Arrays.asList(assets.list(textureDirectory));
			Collections.sort(files);
			return files;
		} catch (IOException e) {
			throw new IllegalArgumentException("Couldn't load texture file names from texture directory");
		}
	}
}
