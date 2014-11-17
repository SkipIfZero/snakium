package com.skipifzero.snakium;

import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES10;
import android.util.Log;

import com.skipifzero.snakium.framework.io.AssetsIO;
import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.Vector2;
import com.skipifzero.snakium.framework.opengl.FileTexture;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.framework.opengl.SpriteBatcher;
import com.skipifzero.snakium.framework.opengl.TextureRegion;

public class BitmapFontRenderer {
	
	//Constants
	private static final int FIRST_CHAR = 32; //First char in unicode table to read.
	private static final int LAST_CHAR = 256; //Last char in unicode table to read. (126 is enough for simple ASCII, 256 to include Swedish characters and some extra.)
	private static final int CHARACTER_COUNT = LAST_CHAR - FIRST_CHAR + 1 + 1; //+1 to include LAST_CHAR and +1 for the unknown char.
	private static final int UNKNOWN_CHAR_INDEX = CHARACTER_COUNT - 1; //The index to the unknown char in the arrays.
	
	public static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	public static final int DEFAULT_SPACING = 1;
	public static final int DEFAULT_MAX_CHAR_CAPACITY = 250;
	
	
	//Tools
	private final int spacing, maxCharCapacity;
	private final SpriteBatcher fontBatcher;
	
	//The bitmap font
	private final FileTexture bitmapFont;
	private final TextureRegion textureRegion;
	private final float[] charWidths;
	private final TextureRegion[] charRegions;
	
	//private final int size;
	private int charRegionWidth, charRegionHeight;
	private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
	private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
	
	//Temporary variables
	private final Vector2 tempVector = new Vector2(0,0);
	private final StringBuilder tempStrBuilder;
	
	public BitmapFontRenderer(AssetManager assetMgr, String texPath, String cfgPath) {
		this(assetMgr, texPath, cfgPath, DEFAULT_BITMAP_CONFIG, DEFAULT_SPACING, DEFAULT_MAX_CHAR_CAPACITY);
	}
	
	public BitmapFontRenderer(AssetManager assetMgr, String texturePath, String configPath, Bitmap.Config texQuality, int spacing, int maxCharCapacity) {
		this.spacing = spacing;
		this.maxCharCapacity = maxCharCapacity;
		
		this.fontBatcher = new SpriteBatcher(maxCharCapacity);
		
		charWidths = new float[CHARACTER_COUNT];
		charRegions = new TextureRegion[CHARACTER_COUNT];
		
		this.bitmapFont = new FileTexture(assetMgr, texturePath, texQuality);
		this.textureRegion = new TextureRegion(bitmapFont, 0, 0, bitmapFont.getWidth(), bitmapFont.getHeight());
		
		processConfigFile(new AssetsIO(assetMgr).readStringsFromFile(configPath));
		
		//Temporary variables
		this.tempStrBuilder = new StringBuilder(maxCharCapacity);
	}
	
	/*
	 * Public methods - Wrappers to make rendering more convenient.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Makes a complete rendering call with the specified parameters.
	 * Calls the internal SpriteBatchers "begin()" and "renderBatch()" methods, so this method must not be
	 * called while another SpriteBatcher is active (including this FontRenderer's internal one).
	 * @param position the position
	 * @param size the size
	 * @param color the color to render
	 * @param string the string to render
	 */
	public void completeDraw(BaseVector2 position, double size, int color, String string) {
		begin(color);
		draw((float)position.getX(), (float)position.getY(), (float)size, string);
		render();
	}
	
	/**
	 * Makes a complete rendering call with the specified parameters.
	 * Calls the internal SpriteBatchers "begin()" and "renderBatch()" methods, so this method must not be
	 * called while another SpriteBatcher is active (including this FontRenderer's internal one).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param color the color to render
	 * @param string the string to render
	 */
	public void completeDraw(double x, double y, double size, int color, String string) {
		begin(color);
		draw((float)x, (float)y, (float)size, string);
		render();
	}
	
	/**
	 * Makes a complete rendering call with the specified parameters.
	 * Calls the internal SpriteBatchers "begin()" and "renderBatch()" methods, so this method must not be
	 * called while another SpriteBatcher is active (including this FontRenderer's internal one).
	 * @param position the position
	 * @param size the size
	 * @param angle the angle
	 * @param color the color to render
	 * @param string the string to render
	 */
	public void completeDraw(BaseVector2 position, double size, double angle, int color, String string) {
		begin(color);
		draw((float)position.getX(), (float)position.getY(), (float)size, (float)angle, string);
		render();
	}
	
	/**
	 * Makes a complete rendering call with the specified parameters.
	 * Calls the internal SpriteBatchers "begin()" and "renderBatch()" methods, so this method must not be
	 * called while another SpriteBatcher is active (including this FontRenderer's internal one).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param angle the angle
	 * @param color the color to render
	 * @param string the string to render
	 */
	public void completeDraw(double x, double y, double size, double angle, int color, String string) {
		begin(color);
		draw((float)x, (float)y, (float)size, (float)angle, string);
		render();
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size.
	 * May only be called after "begin()" and before "render()".
	 * @param position the position
	 * @param size the size
	 * @param string the string to render
	 */
	public void draw(BaseVector2 position, double size, String string) {
		draw((float)position.getX(), (float)position.getY(), (float)size, string);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size.
	 * May only be called after "begin()" and before "render()".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param string the string to render
	 */
	public void draw(double x, double y, double size, String string) {
		draw((float)x, (float)y, (float)size, string);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size and angle.
	 * May only be called after "begin()" and before "render()".
	 * @param position the position
	 * @param size the size
	 * @param angle the angle
	 * @param string the string to render
	 */
	public void draw(BaseVector2 position, double size, double angle, String string) {
		draw((float)position.getX(), (float)position.getY(), (float)size, (float)angle, string);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size and angle.
	 * May only be called after "begin()" and before "render()".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param angle the angle
	 * @param string the string to render
	 */
	public void draw(double x, double y, double size, double angle, String string) {
		draw((float)x, (float)y, (float)size, (float)angle, string);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size. The string
	 * will be split up into substrings where each substring is a row and doesn't exceed the width
	 * of the specified width. Strings will only be split at "empty space", so if you have a word
	 * that is longer than the specified width the behavior of this method is undefined. 
	 * HorizontalAlignment works as usual, but VerticalAlignment takes into account the height of
	 * the whole "block".
	 * @param position the position
	 * @param size the size
	 * @param width the maximum width of the string block
	 * @param string the string to render
	 */
	public void drawRowBreaking(BaseVector2 position, double size, double width, String string) {
		drawRowBreaking((float)position.getX(), (float)position.getY(), (float)size, (float)width, string);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size. The string
	 * will be split up into substrings where each substring is a row and doesn't exceed the width
	 * of the specified width. Strings will only be split at "empty space", so if you have a word
	 * that is longer than the specified width the behavior of this method is undefined. 
	 * HorizontalAlignment works as usual, but VerticalAlignment takes into account the height of
	 * the whole "block".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param width the maximum width of the string block
	 * @param string the string to render
	 */
	public void drawRowBreaking(double x, double y, double size, double width, String string) {
		drawRowBreaking((float)x, (float)y, (float)size, (float)width, string);
	}
	
	/*
	 * Public methods - Rendering methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Starts the internal SpriteBatcher.
	 * All text rendered in this batch will be of the specified color. If you want to render with multiple colors
	 * you will need multiple batches.
	 * @param color the color of the rendered text
	 */
	public void begin(int color) {
		GLES10.glColor4f(Color.red(color)/255f, Color.green(color)/255f, Color.blue(color)/255f, Color.alpha(color)/255f); //Sets color
		fontBatcher.beginBatch(bitmapFont);
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size.
	 * May only be called after "begin()" and before "render()".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param string the string to render
	 */
	public void draw(float x, float y, float size, String string) {
		//Checks if there is anything to render.
		if(size <= 0 || string.length() < 1) {
			return;
		}
		
		//Calculates scaling factor.
		float pixelToInternal = (size / charRegionHeight); //charRegionHeight * pixelToInternal = size
		
		//Fixes alignment by calculating where to start rendering the string.
		float xItr = x + getHorizontalAdjustment(string, size, pixelToInternal);
		float yItr = y + getVerticalAdjustment(pixelToInternal);
		
		//Render the string.
		int arrayLocation = -1;
		for(int i = 0; i < string.length(); i++) {
			arrayLocation = getArrayLocation(string.charAt(i));
			fontBatcher.draw(xItr, yItr, charRegionWidth*pixelToInternal, charRegionHeight*pixelToInternal, charRegions[arrayLocation]);
			xItr += (charWidths[arrayLocation] + spacing)*pixelToInternal;
		}
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size and angle.
	 * May only be called after "begin()" and before "render()".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param angle the angle
	 * @param string the string to render
	 */
	public void draw(float x, float y, float size, float angle, String string) {
		//Checks if there is anything to render.
		if(size <= 0 || string.length() < 1) {
			return;
		}
		
		//Calculates scaling factor.
		float pixelToInternal = (size / charRegionHeight); //charRegionHeight * pixelToInternal = size
		
		//Fixes alignment
		tempVector.set(getHorizontalAdjustment(string, size, pixelToInternal), getVerticalAdjustment(pixelToInternal));
		tempVector.rotate(angle);
		float xItr = x + (float)tempVector.getX();
		float yItr = y + (float)tempVector.getY();
		
		//Render the string.
		int arrayLocation = -1;
		for(int i = 0; i < string.length(); i++) {
			arrayLocation = getArrayLocation(string.charAt(i));
			fontBatcher.draw(xItr, yItr, charRegionWidth*pixelToInternal, charRegionHeight*pixelToInternal, angle, charRegions[arrayLocation]);
			tempVector.makeUnit(angle).mult((charWidths[arrayLocation] + spacing)*pixelToInternal);
			xItr += (float)tempVector.getX();
			yItr += (float)tempVector.getY();
		}
	}
	
	/**
	 * Draws the specified string at the specified coordinates with the specified size. The string
	 * will be split up into substrings where each substring is a row and doesn't exceed the width
	 * of the specified width. Strings will only be split at "empty space", so if you have a word
	 * that is longer than the specified width the behavior of this method is undefined. 
	 * HorizontalAlignment works as usual, but VerticalAlignment takes into account the height of
	 * the whole "block".
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param size the size
	 * @param width the maximum width of the string block
	 * @param string the string to render
	 */
	@SuppressWarnings("boxing") //TODO: Petor am cry.
	public void drawRowBreaking(float x, float y, float size, float width, String string) {
		//Checks if there is anything to render.
		if(size <= 0 || string.length() < 1) {
			return;
		}
		
		//Checks if width is valid
		if(width < 0) {
			return;
		}
		
		//Deletes and appends string to temporary StringBuilder.
		tempStrBuilder.delete(0, tempStrBuilder.length());
		tempStrBuilder.append(string).append(" "); //Hack to make sure there is an index at the end of the string.
		
		//Creates substrings for each row
		List<Integer> spaceIndices = getSpaceIndicies(tempStrBuilder.toString()); //Gets index for each space in string
		List<String> subStrings = new ArrayList<String>();
		int startIndex = 0;
		int lastIndex = 0;
		String str = null;
		String lastStr = null;
		for(Integer currentIndex : spaceIndices) {
			str = tempStrBuilder.substring(startIndex, currentIndex);
			if(renderedStringWidth(str, size) >= width) {
				subStrings.add(lastStr);
				startIndex = lastIndex + 1; //+1 so it doesn't include "space" first on next line.
			}
			lastIndex = currentIndex;
			lastStr = str;
		}
		subStrings.add(tempStrBuilder.substring(startIndex));
		
		//Calculates yPos for vertical alignment.
		float yPos;
		switch(verticalAlignment) {
			case TOP:
				yPos = y;
				break;	
			case CENTER:
				yPos = y + (subStrings.size()*size)/2;
				break;
			case BOTTOM:
				yPos = y + (subStrings.size()*size);
				break;
			default:
				throw new AssertionError(); //Should never happen.
		}
		
		//Draws each substring on the correct yPos.
		for(String subStr : subStrings) {
			draw(x, yPos, size, subStr);
			yPos -= size;
		}
	}
	
	/**
	 * Renders the batched strings.
	 * Also restores the default color (ARGB:1,1,1,1) with "glColor4f()".
	 * This method may only be called after "begin()" and at least one "draw()" method has been called.
	 * After it has been called the previous condition must be re-fulfilled before it may be called again.
	 */
	public void render() {
		fontBatcher.renderBatch();
		GLES10.glColor4f(1f, 1f, 1f, 1f); //Restores default color (ARGB: 255, 255, 255, 255).
	}
	
	/*
	 * Public methods - Miscellaneous methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets the horizontal alignment used when rendering text.
	 * @param horizontalAlignment the horizontal alignment.
	 */
	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	/**
	 * Sets the vertical alignment used when rendering text.
	 * @param verticalAlignment the vertical alignment.
	 */
	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
	
	/**
	 * Reloads the FontRenderer.
	 */
	public void reload() {
		bitmapFont.reload();
	}
	
	/**
	 * Disposes of the texture used for the generated bitmap font.
	 */
	public void dispose() {
		bitmapFont.dispose();
	}
	
	/**
	 * Draws the bitmap font to the specified coordinates at the specified size.
	 * Calls the internal SpriteBatchers "begin()" and "renderBatch()" methods, so this method must not be
	 * called while another SpriteBatcher is active (including this FontRenderer's internal one).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width
	 * @param height the height
	 */
	public void drawBitmapTexture(double x, double y, double width, double height) {
		fontBatcher.beginBatch(bitmapFont);
		fontBatcher.draw(x, y, width, height, textureRegion);
		fontBatcher.renderBatch();
	}
	
	/**
	 * Returns the width of the specified string rendered at the specified size.
	 * @param string the string.
	 * @param size the render size.
	 * @return the width of the string rendered
	 */
	public double renderedStringWidth(String string, double size) {
		double pixelToInternal = size / charRegionHeight; //charRegionHeight * pixelToInternal = size
		
		double stringWidth = 0;
		for(int i = 0; i < string.length(); i++) {
			stringWidth += (charWidths[getArrayLocation(string.charAt(i))] + spacing)*pixelToInternal;
		}
		stringWidth -= spacing*pixelToInternal; //Removes spacing width from last character.
		
		return stringWidth;
	}
	
	/**
	 * Returns the max char capacity of the internal SpriteBatcher.
	 * @return max char capacity
	 */
	public int getMaxCharCapacity() {
		return maxCharCapacity;
	}
	
	/*
	 * Private methods - Rendering utilities
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private float getHorizontalAdjustment(String string, float size, float pixelToInternal) {
		float width = charRegionWidth * pixelToInternal;
		switch(this.horizontalAlignment) {
			case LEFT:
				return width/2;
			case RIGHT:
				return width/2 - (float)renderedStringWidth(string, size);
			case CENTER:
				return width/2 - (float)(renderedStringWidth(string, size)/2);
			default:
				throw new AssertionError();
		}
	}
	
	private float getVerticalAdjustment(float pixelToInternal) {
		float height = charRegionHeight*pixelToInternal;
		switch(this.verticalAlignment) {
			case TOP:
				return -height/2;
			case BOTTOM:
				return height/2;
			case CENTER:
				return 0;
			default:
				throw new AssertionError();
		}
	}
	
	private static int getArrayLocation(char c) {
		if(c < FIRST_CHAR || c > LAST_CHAR) {
			return UNKNOWN_CHAR_INDEX;
		}
		
		return c - FIRST_CHAR;
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private void processConfigFile(List<String> rows) {
		if(rows.size() < CHARACTER_COUNT+1) {
			throw new IllegalArgumentException("Not enough String rows in config.");
		}
		
		// 1st line: "charRegionWidth;charRegionHeight"
		String[] charRegionSizeParts = rows.get(0).split(";");
		this.charRegionWidth = Integer.parseInt(charRegionSizeParts[0]);
		this.charRegionHeight = Integer.parseInt(charRegionSizeParts[1]);
		
		//boolean[] arrayIndices = new boolean[CHARACTER_COUNT];
		//Arrays.fill(arrayIndices, false);
		
		// Format: "arraylocation;charwidth;texreg_xpos;texreg_ypos;texreg_width;texreg_height"
		for(int i = 1; i < rows.size(); i++) { //Starts at 1, skips first line.
			if(rows.get(i).length() <= 0) {
				continue;
			}
			String[] parts = rows.get(i).split(";");
			int arrayIndex = Integer.parseInt(parts[0]);
			//arrayIndices[arrayIndex] = true;
			charWidths[arrayIndex] = Float.parseFloat(parts[1]);
			charRegions[arrayIndex] = new TextureRegion(bitmapFont, Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
		}
		
//		for(boolean bool : arrayIndices) {
//			if(!bool) {
//				throw new IllegalArgumentException("Config doesn't contain all required array indices.");
//			}
//		}
	}
	
	@SuppressWarnings("boxing") //TODO: Petor am cry.
	private static List<Integer> getSpaceIndicies(String string) {
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < string.length(); i++) {
			if(string.charAt(i) == ' ') {
				indices.add(i);
			}
		}
		return indices;
	}
}
