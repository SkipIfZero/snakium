package com.skipifzero.snakium;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.skipifzero.snakium.framework.io.USBIO;
import com.skipifzero.snakium.framework.opengl.BitmapTexture;
import com.skipifzero.snakium.framework.opengl.Texture;
import com.skipifzero.snakium.framework.opengl.TextureRegion;

public class BitmapFontGenerator {

	/*
	 * Non-builder variables
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	//Constants
	private static final int FIRST_CHAR = 32; //First char in unicode table to read.
	private static final int LAST_CHAR = 256; //Last char in unicode table to read. (126 is enough for simple ASCII, 256 to include Swedish characters and some extra.)
	private static final int CHARACTER_COUNT = LAST_CHAR - FIRST_CHAR + 1 + 1; //+1 to include LAST_CHAR and +1 for the unknown char.
	private static final int UNKNOWN_CHAR = 32; //Char used for unknown input. (In this case space).
	private static final int UNKNOWN_CHAR_INDEX = CHARACTER_COUNT - 1; //The index to the unknown char in the arrays.
	
	//Variables
	private Texture bitmapFont;
	private TextureRegion textureRegion;
	private final TextureRegion[] charRegions;
	private final float[] charWidths;
	private int charRegionWidth, charRegionHeight;
	private int cellWidth, cellHeight;
	private int textureSize;
	
	/*
	 * Builder variables
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static final Typeface DEFAULT_FONT = Typeface.DEFAULT;
	public static final int DEFAULT_SIZE = 32;
	public static final int DEFAULT_X_PADDING = 1;
	public static final int DEFAULT_Y_PADDING = 1;
	public static final int DEFAULT_COLOR = 0xffffffff;
	
	private Typeface font;
	private int size;
	private int xPadding;
	private int yPadding;
	private int color;
	
	/*
	 * Public methods - Builder methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public BitmapFontGenerator () {
		reset();
		charWidths = new float[CHARACTER_COUNT];
		charRegions = new TextureRegion[CHARACTER_COUNT];
	}
	
	/**
	 * Resets all settings to their defaults.
	 * @return this
	 */
	public BitmapFontGenerator reset() {
		this.font = DEFAULT_FONT;
		this.size = DEFAULT_SIZE;
		this.xPadding = DEFAULT_X_PADDING;
		this.yPadding = DEFAULT_Y_PADDING;
		this.color = DEFAULT_COLOR;
		return this;
	}
	
	/**
	 * Sets the font to be used in the FontRenderer
	 * @param font the font
	 * @return this
	 */
	public BitmapFontGenerator setFont(Typeface font) {
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
	public BitmapFontGenerator setSize(int size) {
		if(size <= 0) {
			throw new IllegalArgumentException("Size must be > 0");
		}
		this.size = size;
		return this;
	}
	
	/**
	 * Sets the xPadding between characters on the generated bitmap font.
	 * This should only be touched if you get problems with artifacts around your rendered characters.
	 * @param xPadding
	 * @throws IllegalArgumentException if xPadding < 0
	 * @return this
	 */
	public BitmapFontGenerator setXPadding(int xPadding) {
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
	public BitmapFontGenerator setYPadding(int yPadding) {
		if(yPadding < 0) {
			throw new IllegalArgumentException("yPadding must be >= 0");
		}
		this.yPadding = yPadding;
		return this;
	}
	
	public BitmapFontGenerator setColor(int color) {
		this.color = color;
		return this;
	}
	
	public void build(String configPath, String bitmapPath) {
		Bitmap bitmap = load();
		List<String> config = buildConfig();
		
		USBIO.writeStringsToFile(configPath, config);
		USBIO.writeBitmapToFile(bitmapPath, bitmap);
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private List<String> buildConfig() {
		List<String> rows = new ArrayList<String>();
		
		// 1st line: "charRegionWidth;charRegionHeight"
		rows.add(charRegionWidth + ";" + charRegionHeight);
		
		StringBuilder sb = new StringBuilder(200);
		int texWidth = bitmapFont.getWidth();
		int texHeight = bitmapFont.getHeight();
		// Format: "arraylocation;charwidth;texreg_xpos;texreg_ypos;texreg_width;texreg_height"
		for(int i = 0; i < CHARACTER_COUNT; i++) {
			TextureRegion texReg = charRegions[i];
			
			sb.delete(0, sb.length());
			sb.append(i).append(';') //arraylocation
			.append(charWidths[i]).append(';') //charwidth
			.append(texReg.u1 * texWidth).append(';') //texreg_xpos
			.append(texReg.v1 * texHeight).append(';') //texreg_ypos
			.append(texReg.width).append(';') //texreg_width
			.append(texReg.height); //texreg_height
			
			rows.add(sb.toString());
		}
		
		return rows;
	}
	
	private Bitmap load() {
		Paint paint = getPaintFromTypeface();
		getCharacterWidths(paint); 
		calculateVariousSizes(paint); //TODO: At least this needs to be done.
		Bitmap bitmap = generateTexture(paint);
		getTextureRegions();
		return bitmap;
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
	
	private Bitmap generateTexture(Paint paint) {
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888);
		Log.d("FontGenerator", "Created new Bitmap with size: " + textureSize + "x" + textureSize);
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0x00000000); //Transparent Background
		
		drawCharsToBitmap(canvas, paint);
		
		this.bitmapFont = new BitmapTexture(bitmap);
		textureRegion = new TextureRegion(bitmapFont, 0, 0, bitmapFont.getWidth(), bitmapFont.getHeight());
		return bitmap;
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
		paint.setColor(color);
		paint.setTypeface(font);
		return paint;
	}
}
