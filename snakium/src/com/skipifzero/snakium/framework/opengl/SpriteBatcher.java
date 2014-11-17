package com.skipifzero.snakium.framework.opengl;

import android.opengl.GLES10;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.BoundingRectangle;

/**
 * Class used for rendering sprites quickly with OpenGL.
 * 
 * How to use:
 * First you need a Texture containing a texture atlas, and TextureRegions with specified
 * locations on said atlas. Then you begin a batch with the texture and draw the specified
 * regions. Then you call renderBatch and everything will be drawn.
 * 
 * You cannot use more than one SpriteBatcher simultaneously, and you have to be careful to not
 * do more draws per batch than your specified capacity.
 * 
 * @author Peter Hillerström
 * @since 2013-05-14
 * @version 1
 */

public class SpriteBatcher {
	
	private final float[] verticesBuffer;
	private final Vertices vertices;
	private int bufferIndex, spriteAmount;
	
	/**
	 * Creates a new SpriteBatcher with the specified capacity.
	 * @param capacity the capacity of this SpriteBatcher
	 */
	public SpriteBatcher(int capacity) {
		this.verticesBuffer = new float[capacity*4*4]; //4 vertices per sprite, 4 floats per vertex.
		this.vertices = new Vertices(capacity*4, capacity*6, false, true); //4 vertices per sprite, max 6 indices per sprite.
		
		this.bufferIndex = 0;
		this.spriteAmount = 0;
		
		short[] indices = new short[capacity*6]; //6 indices per sprite.
		//Sets the indices in the correct pattern for each sprite. First is 0 1 2 2 3 0, second is 4 5 6 6 7 4, etc.
		short j = 0;
		for(int i = 0; i < indices.length; i += 6, j += 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}
		vertices.setIndices(indices, 0, indices.length);
	}
	
	/**
	 * Begins a new batch with the specified Texture.
	 * @param texture the Texture
	 */
	public void beginBatch(Texture texture) {
		texture.bind();
		bufferIndex = 0;
		spriteAmount = 0;
	}
	
	/**
	 * Renders the batched draws.
	 */
	public void renderBatch() {
		if(spriteAmount == 0) {
			return;
		}
		vertices.setVertices(verticesBuffer, 0, bufferIndex);
		vertices.bind();
		vertices.draw(GLES10.GL_TRIANGLES, 0, spriteAmount * 6);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified coordinates.
	 * @param x the center x coordinate
	 * @param y the center y coordinate
	 * @param width the width
	 * @param height the height
	 * @param texRegion the TextureRegion
	 */
	public void draw(double x, double y, double width, double height, TextureRegion texRegion) {
		draw((float)x, (float)y, (float)width, (float)height, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position.
	 * @param centerPosition the position
	 * @param width the width
	 * @param height the height
	 * @param texRegion the TextureRegion
	 */
	public void draw(BaseVector2 centerPosition, double width, double height, TextureRegion texRegion) {
		draw((float)centerPosition.getX(), (float)centerPosition.getY(), (float)width, (float)height, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position.
	 * @param centerPosition the position
	 * @param dimension the dimension vector
	 * @param texRegion the TextureRegion
	 */
	public void draw(BaseVector2 centerPosition, BaseVector2 dimension, TextureRegion texRegion) {
		draw((float)centerPosition.getX(), (float)centerPosition.getY(), (float)dimension.getX(), (float)dimension.getY(), texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position.
	 * @param shape the BoundingRectangle with the size and position
	 * @param texRegion the TextureRegion
	 */
	public void draw(BoundingRectangle shape, TextureRegion texRegion) {
		draw((float)shape.getPosition().getX(), (float)shape.getPosition().getY(), (float)shape.getWidth(), (float)shape.getHeight(), texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified coordinates rotated to the specified angle.
	 * @param x the center x coordinate
	 * @param y the center y coordinate
	 * @param width the width
	 * @param height the height
	 * @param angle the angle
	 * @param texRegion the TextureRegion
	 */
	public void draw(double x, double y, double width, double height, double angle, TextureRegion texRegion) {
		draw((float)x, (float)y, (float)width, (float)height, (float)angle, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position rotated to the specified angle.
	 * @param centerPosition the position
	 * @param width the width
	 * @param height the height
	 * @param angle the angle
	 * @param texRegion the TextureRegion
	 */
	public void draw(BaseVector2 centerPosition, double width, double height, double angle, TextureRegion texRegion) {
		draw((float)centerPosition.getX(), (float)centerPosition.getY(), (float)width, (float)height, (float)angle, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position rotated to the specified angle.
	 * @param centerPosition the position
	 * @param dimension the dimension vector
	 * @param angle the angle
	 * @param texRegion the TextureRegion
	 */
	public void draw(BaseVector2 centerPosition, BaseVector2 dimension, double angle, TextureRegion texRegion) {
		draw((float)centerPosition.getX(), (float)centerPosition.getY(), (float)dimension.getX(), (float)dimension.getY(), (float)angle, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified position rotated to the specified angle.
	 * @param shape the BoundingRectangle with the size and position
	 * @param angle the angle
	 * @param texRegion the TextureRegion
	 */
	public void draw(BoundingRectangle shape, double angle, TextureRegion texRegion) {
		draw((float)shape.getPosition().getX(), (float)shape.getPosition().getY(), (float)shape.getWidth(), (float)shape.getHeight(), angle, texRegion);
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified coordinates.
	 * @param x the center x coordinate
	 * @param y the center y coordinate
	 * @param width the width
	 * @param height the height
	 * @param texRegion the TextureRegion
	 */
	public void draw(float x, float y, float width, float height, TextureRegion texRegion) {
		float halfWidth = width/2;
		float halfHeight = height/2;
		
		float x1 = x - halfWidth;
		float y1 = y - halfHeight;
		float x2 = x + halfWidth;
		float y2 = y + halfHeight;
		
		//Vertex 1
		//Position
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u1;
		verticesBuffer[bufferIndex++] = texRegion.v2;
		
		//Vertex 2
		//Position
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y1;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u2;
		verticesBuffer[bufferIndex++] = texRegion.v2;
		
		//Vertex 3
		//Position
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u2;
		verticesBuffer[bufferIndex++] = texRegion.v1;
		
		//Vertex 4
		//Position
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y2;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u1;
		verticesBuffer[bufferIndex++] = texRegion.v1;
		
		spriteAmount++; //One sprite batched.
	}
	
	/**
	 * Draws the specified TextureRegion with the specified size to the specified coordinates rotated to the specified angle.
	 * @param x the center x coordinate
	 * @param y the center y coordinate
	 * @param width the width
	 * @param height the height
	 * @param angle the angle
	 * @param texRegion the TextureRegion
	 */
	public void draw(float x, float y, float width, float height, float angle, TextureRegion texRegion) {
		float halfWidth = width/2;
		float halfHeight = height/2;
		
		double radAngle = angle * BaseVector2.DEG_TO_RAD;
		float cosAng = (float)Math.cos(radAngle);
		float sinAng = (float)Math.sin(radAngle);
		
		//Rotates TextureRegion. Done with rotation matrix.
		float x1 = -halfWidth * cosAng - (-halfHeight) * sinAng;
		float y1 = -halfWidth * sinAng + (-halfHeight) * cosAng;
		float x2 = halfWidth * cosAng - (-halfHeight) * sinAng;
		float y2 = halfWidth * sinAng + (-halfHeight) * cosAng;
		float x3 = halfWidth * cosAng - halfHeight * sinAng;
		float y3 = halfWidth * sinAng + halfHeight * cosAng;
		float x4 = -halfWidth * cosAng - halfHeight * sinAng;
		float y4 = -halfWidth * sinAng + halfHeight * cosAng;
		
		//Moves TextureRegion to its position.
		x1 += x;
		y1 += y;
		x2 += x;
		y2 += y;
		x3 += x;
		y3 += y;
		x4 += x;
		y4 += y;
		
		//Vertex 1
		//Position
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u1;
		verticesBuffer[bufferIndex++] = texRegion.v2;
		
		//Vertex 2
		//Position
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u2;
		verticesBuffer[bufferIndex++] = texRegion.v2;
		
		//Vertex 3
		//Position
		verticesBuffer[bufferIndex++] = x3;
		verticesBuffer[bufferIndex++] = y3;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u2;
		verticesBuffer[bufferIndex++] = texRegion.v1;
		
		//Vertex 4
		//Position
		verticesBuffer[bufferIndex++] = x4;
		verticesBuffer[bufferIndex++] = y4;
		//Texture Mapping
		verticesBuffer[bufferIndex++] = texRegion.u1;
		verticesBuffer[bufferIndex++] = texRegion.v1;
		
		spriteAmount++; //One sprite batched.
	}
}
