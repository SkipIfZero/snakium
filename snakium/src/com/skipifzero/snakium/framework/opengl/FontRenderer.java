package com.skipifzero.snakium.framework.opengl;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES10;
import android.util.Log;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * A FontRenderer for OpenGL ES 1.0.
 * Uses static calls (GLES10).
 * Uses a SpriteBatcher, which means you have to call "begin()" before your drawing calls and "render()" afterwards.
 * Unless you use the "drawComplete()" methods which do that for you, however those might be a bit slower.
 * You can only have on SpriteBatcher running at a time, so you can't render some text in the middle of rendering
 * sprites.
 * 
 * Based on http://fractiousg.blogspot.se/2012/04/rendering-text-in-opengl-on-android.html.
 * 
 * @version 2
 * @since 2013-05-08
 * @author Peter Hillerström
 */
public class FontRenderer {
	
	/**
	 * Builder used for building a FontRenderer.
	 * Everything has defaults, you don't really need to specify anything if you don't want to.
	 */
	public static class Builder {
		public static final Typeface DEFAULT_FONT = Typeface.DEFAULT;
		public static final int DEFAULT_SIZE = 32;
		public static final int DEFAULT_SPACING = 1;
		public static final int DEFAULT_MAX_CHAR_CAPACITY = 250;
		public static final int DEFAULT_X_PADDING = 1;
		public static final int DEFAULT_Y_PADDING = 1;
		public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.LEFT;
		public static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.CENTER;
		
		private Typeface font;
		private int size;
		private int spacing;
		private int maxCharCapacity;
		private int xPadding;
		private int yPadding;
		private HorizontalAlignment horizontalAlignment;
		private VerticalAlignment verticalAlignment;
		
		public Builder() {
			reset();
		}
		
		/**
		 * Resets all settings to their defaults.
		 * @return this
		 */
		public Builder reset() {
			this.font = DEFAULT_FONT;
			this.size = DEFAULT_SIZE;
			this.spacing = DEFAULT_SPACING;
			this.maxCharCapacity = DEFAULT_MAX_CHAR_CAPACITY;
			this.xPadding = DEFAULT_X_PADDING;
			this.yPadding = DEFAULT_Y_PADDING;
			this.horizontalAlignment = DEFAULT_HORIZONTAL_ALIGNMENT;
			this.verticalAlignment = DEFAULT_VERTICAL_ALIGNMENT;
			return this;
		}
		
		/**
		 * Sets the font to be used in the FontRenderer
		 * @param font the font
		 * @return this
		 */
		public Builder setFont(Typeface font) {
			this.font = font;
			return this;
		}
		
		/**
		 * Sets the size of each character on the bitmap font.
		 * Larger usually means higher quality, but will end up taking up more space in memory
		 * and too large might not fit the largest texture size.
		 * @param size
		 * @throws IllegalArgumentException if size <= 0
		 * @return this
		 */
		public Builder setSize(int size) {
			if(size <= 0) {
				throw new IllegalArgumentException("Size must be > 0");
			}
			this.size = size;
			return this;
		}
		
		/**
		 * Sets the spacing between rendered characters.
		 * @param spacing
		 * @throws IllegalArgumentException if spacing < 0
		 * @return this
		 */
		public Builder setSpacing(int spacing) {
			if(spacing < 0) {
				throw new IllegalArgumentException("Spacing must be >= 0");
			}
			this.spacing = spacing;
			return this;
		}
		
		/**
		 * Sets the maximum char capacity of the FontRenderer. This is the amount of chars you can render
		 * in one batch.
		 * @param maxCharCapacity
		 * @throws IllegalArgumentException if maxCharCapacity < 10
		 * @return this
		 */
		public Builder setMaxCharCapacity(int maxCharCapacity) {
			if(maxCharCapacity < 10) {
				throw new IllegalArgumentException("maxCharCapacity must be >= 10");
			}
			this.maxCharCapacity = maxCharCapacity;
			return this;
		}
		
		/**
		 * Sets the xPadding between characters on the generated bitmap font.
		 * This should only be touched if you get problems with artifacts around your rendered characters.
		 * @param xPadding
		 * @throws IllegalArgumentException if xPadding < 0
		 * @return this
		 */
		public Builder setXPadding(int xPadding) {
			if(xPadding < 0) {
				throw new IllegalArgumentException("xPadding must be >= 0");
			}
			this.xPadding = xPadding;
			return this;
		}
		
		/**
		 * Sets the yPadding between characters on the generated bitmap font.
		 * This should only be touched if you get problems with artifacts around your rendered characters.
		 * @param xPadding
		 * @throws IllegalArgumentException if yPadding < 0
		 * @return this
		 */
		public Builder setYPadding(int yPadding) {
			if(yPadding < 0) {
				throw new IllegalArgumentException("yPadding must be >= 0");
			}
			this.yPadding = yPadding;
			return this;
		}
		
		/**
		 * Sets the horizontal alignment.
		 * @param horizontalAlignment
		 * @return this
		 */
		public Builder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
			this.horizontalAlignment = horizontalAlignment;
			return this;
		}
		
		/**
		 * Sets the vertical alignment
		 * @param verticalAlignment
		 * @return this
		 */
		public Builder setVerticalAlignment(VerticalAlignment verticalAlignment) {
			this.verticalAlignment = verticalAlignment;
			return this;
		}
		
		/**
		 * Builds the FontRenderer with the specified settings.
		 * @return a new FontRenderer
		 */
		public FontRenderer build() {
			return new FontRenderer(font, size, spacing, maxCharCapacity, xPadding, yPadding, horizontalAlignment, verticalAlignment);
		}
	}
	
	/**
	 * Enum used for setting horizontal alignment of rendered text.
	 */
	public enum HorizontalAlignment {
		LEFT, CENTER, RIGHT;
	}
	
	/**
	 * Enum used for setting vertical alignment of rendered text.
	 */
	public enum VerticalAlignment {
		TOP, CENTER, BOTTOM;
	}
	
	//Constants
	private static final int FIRST_CHAR = 32; //First char in unicode table to read.
	private static final int LAST_CHAR = 256; //Last char in unicode table to read. (126 is enough for simple ASCII, 256 to include Swedish characters and some extra.)
	private static final int CHARACTER_COUNT = LAST_CHAR - FIRST_CHAR + 1 + 1; //+1 to include LAST_CHAR and +1 for the unknown char.
	private static final int UNKNOWN_CHAR = 32; //Char used for unknown input. (In this case space).
	private static final int UNKNOWN_CHAR_INDEX = CHARACTER_COUNT - 1; //The index to the unknown char in the arrays.
	
	//Tools
	private final int maxCharCapacity;
	private final SpriteBatcher fontBatcher;
	
	//The font
	private final Typeface font;
	
	//The bitmap font
	private Texture bitmapFont;
	private TextureRegion textureRegion;
	private final TextureRegion[] charRegions;
	private final float[] charWidths;
	
	private final int size;
	private final int spacing;
	private final int xPadding, yPadding;
	private int charRegionWidth, charRegionHeight;
	private int cellWidth, cellHeight;
	private int textureSize;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;
	
	//Temporary variables
	private final Vector2 tempVector = new Vector2(0,0);
	private final StringBuilder tempStrBuilder;
	
	private FontRenderer(Typeface font, int size, int spacing, int maxCharCapacity, int xPadding, int yPadding, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		this.font = font;
		this.size = size;
		this.spacing = spacing;
		this.maxCharCapacity = maxCharCapacity;
		this.xPadding = xPadding;
		this.yPadding = yPadding;
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		
		this.fontBatcher = new SpriteBatcher(maxCharCapacity);
		
		charWidths = new float[CHARACTER_COUNT];
		charRegions = new TextureRegion[CHARACTER_COUNT];
		
		load();
		
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
		load();
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
	 * Private methods - Creation methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private void load() {
		Paint paint = getPaintFromTypeface();
		getCharacterWidths(paint);
		calculateVariousSizes(paint);
		generateTexture(paint);
		getTextureRegions();
	}
	
	private void getTextureRegions() {		
		float x = 0;
		float y = 0;
		
		for(int arrayLocation = 0; arrayLocation < CHARACTER_COUNT; arrayLocation++) {
			charRegions[arrayLocation] = new TextureRegion(bitmapFont, x + xPadding, y + yPadding, charRegionWidth, charRegionHeight);
			
			x += cellWidth;
			if((x + cellWidth) > textureSize) {
				x = 0;
				y += cellHeight;
			}
		}
	}
	
	private void generateTexture(Paint paint) {
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8);
		Log.d("FontRenderer", "Created new Bitmap with size: " + textureSize + "x" + textureSize);
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0x00000000); //Transparent Background
		
		drawCharsToBitmap(canvas, paint);		
		
		this.bitmapFont = new BitmapTexture(bitmap);
		textureRegion = new TextureRegion(bitmapFont, 0, 0, bitmapFont.getWidth(), bitmapFont.getHeight());
	}

	private void drawCharsToBitmap(Canvas canvas, Paint paint) {
		float fontDescent = (float)Math.ceil( Math.abs(paint.getFontMetrics().descent) ); //Length between fonts baseline and "descending" parts of font.
		
		//Calculates position of first char.
		float x = xPadding; 
		float y = (cellHeight - 1) - fontDescent - yPadding;
		
		//Loops through all chars and draws them to bitmap.
		char[] charArray = new char[1];
		for(char c = FIRST_CHAR; c <= LAST_CHAR; c++) {
			charArray[0] = c;
			canvas.drawText(charArray, 0, 1, x, y, paint); //Draws char to array at designated position.
			
			//Calculates next position.
			x += cellWidth;
			if((x + cellWidth - xPadding) > textureSize) {
				x = xPadding;
				y += cellHeight;
			}
		}
		
		//Draws the Unknown character last.
		charArray[0] = UNKNOWN_CHAR;
		canvas.drawText(charArray, 0, 1, x, y, paint);
	}
	
	private void calculateVariousSizes(Paint paint) {
		//Calculate charRegionWidth and cellWidth
		float maxFontWidth = 0;
		for(float width : charWidths) {
			if(width > maxFontWidth) {
				maxFontWidth = width;
			}
		}
		this.charRegionWidth = (int)Math.ceil(maxFontWidth);
		this.cellWidth = charRegionWidth + (2*xPadding);
		
		//Calculate charRegionHeight and cellHeight
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		float maxFontHeight = (float) Math.ceil( Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top) );
		this.charRegionHeight = (int)Math.ceil(maxFontHeight);
		this.cellHeight = charRegionHeight + (2*yPadding);
		
		//Calculate cellSize
		int cellSize = cellWidth > cellHeight ? cellWidth : cellHeight;
		
		//Calculate textureSize (loops through various texture sizes until it finds one where all specified characters fit.
		for(int textureSize = 128; textureSize <= 8192; textureSize *= 2) {
			int cellsPerRowOrCol = textureSize / cellSize;
			if(cellsPerRowOrCol*cellsPerRowOrCol >= CHARACTER_COUNT) {
				this.textureSize = textureSize;
				break;
			}
		}
		//Makes sure textureSize was set.
		if(this.textureSize < 128) {
			throw new RuntimeException("Couldn't create a large enough texture to hold bitmap font.");
		}
	}

	private void getCharacterWidths(Paint paint) {
		char[] charArray = new char[1];
		float[] widthArray = new float[1];
		int arrayLocation = 0;
		
		for(char c = FIRST_CHAR; c <= LAST_CHAR; c++) {
			charArray[0] = c;
			paint.getTextWidths(charArray, 0, 1, widthArray);
			charWidths[arrayLocation] = widthArray[0];
			arrayLocation++;
		}
		
		//Gets unknown char width
		char c = UNKNOWN_CHAR;
		charArray[0] = c;
		paint.getTextWidths(charArray, 0, 1, widthArray);
		charWidths[arrayLocation] = widthArray[0];
	}

	private Paint getPaintFromTypeface() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(size);
		paint.setColor(0xffffffff); //Set ARGB (White, Opaque).
		paint.setTypeface(font);
		return paint;
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
