package com.skipifzero.snakium.framework.opengl;


/**
 * A class containing a region of a Texture.
 * 
 * Primarily used to store multiple sprites on the same Texture and enable use of a SpriteBatcher.
 * 
 * @author Peter Hillerström
 * @version 1
 */
public class TextureRegion {
	public final float u1, v1;
	public final float u2, v2;
	public final float width, height;
	
	/**
	 * Creates a new TextureRegion.
	 * @param texture the Texture
	 * @param x the top left x coordinate
	 * @param y the top left y coordinate
	 * @param width the width of the TextureRegion
	 * @param height the height of the TextureRegion
	 */
	public TextureRegion(Texture texture, double x, double y, double width, double height) {
		this(texture, (float)x, (float)y, (float)width, (float)height);
	}
	
	/**
	 * Creates a new TextureRegion.
	 * @param texture the Texture
	 * @param x the top left x coordinate
	 * @param y the top left y coordinate
	 * @param width the width of the TextureRegion
	 * @param height the height of the TextureRegion
	 */
	public TextureRegion(Texture texture, float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		
		int texWidth = texture.getWidth();
		int texHeight = texture.getHeight();
		
		//TopLeft texture point.
		u1 = x / texWidth;
		v1 = y / texHeight;
		//BottomRight texture point.
		u2 = u1 + width / texWidth;
		v2 = v1 + height / texHeight;
	}
	
	/**
	 * Returns the width of this TextureRegion
	 * @return width
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of this TextureRegion
	 * @return height
	 */
	public double getHeight() {
		return height;
	}
}
