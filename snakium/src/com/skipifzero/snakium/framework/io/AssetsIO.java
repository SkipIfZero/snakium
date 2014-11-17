package com.skipifzero.snakium.framework.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;

/**
 * A class used for various Assets IO operations.
 * @author Peter Hillerström
 * @version 2
 */

public class AssetsIO {
	
	private final AssetManager assets;
	
	public AssetsIO(AssetManager assets){
		this.assets = assets;
	}
	
	/**
	 * Loads a bitmap from the assets folder with the specified name.
	 * The quality is not guaranteed, just a suggestion.
	 * @param fileName the path to the bitmap in the assets folder
	 * @param config the suggested quality
	 * @throws RuntimeException if it couldn't load the bitmap.
	 * @return loaded bitmap
	 */
	public Bitmap loadBitmap(String fileName, Bitmap.Config config){
		Options options = new Options();
		options.inPreferredConfig = config;
		
		Bitmap bitmap = null;
		InputStream in = null;
		
		//Load bitmap.
		try {
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
		} catch(IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset file: \"" + fileName + "\"");
		} finally {
			//Try to close the input stream.
			if(in != null){
				try{
					in.close();
				}catch(IOException e){
					//Do nothing.
				}
			}
		}
		
		if(bitmap == null) { //Throws a RuntimeException if it all somehow failed.
			throw new RuntimeException("Couldn't load bitmap from asset file: \"" + fileName + "\"");
		}
		
		return bitmap;
	}
	
	public List<String> readStringsFromFile(String path) {		
		List<String> rows = new ArrayList<String>();
		
		//Reads rows
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(assets.open(path)));
			while(true) {
				String currLine = bf.readLine();
				if(currLine == null) {
					break;
				}
				rows.add(currLine);
			}
		} catch(IOException e) {
			throw new RuntimeIOException("Problem reading file: " + e.getMessage());
		} finally {
			try {
				if(bf != null) {
					bf.close();
				}
			} catch(IOException e) {
				//Do nothing.
			}
		}
		
		return rows;
	}
	
	/**
	 * Loads a font from the assets folder.
	 * @param fileName the path to the font in the assets folder
	 * @return the font
	 */
	public Typeface loadFont(String fileName){
		return Typeface.createFromAsset(assets, fileName);
	}
}
