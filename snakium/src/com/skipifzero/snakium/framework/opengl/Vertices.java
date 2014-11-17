package com.skipifzero.snakium.framework.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES10;

/**
 * A class used for storing Vertices.
 * @author Peter Hillerström
 * @version 1
 */
public class Vertices {
	
	private final boolean hasColor, hasTexture;
	private final int vertexSize;
	private static final int INDICES_SIZE = 2;
	
	private final FloatBuffer vertices;
	private final ShortBuffer indices;
	
	public Vertices(int maxAmountOfVertices, int maxAmountOfIndices, boolean hasColor, boolean hasTexture) {
		this.hasColor = hasColor;
		this.hasTexture = hasTexture;
		this.vertexSize = (2 + (hasColor?4:0) + (hasTexture?2:0)) * 4; //2 coordinates, 4 color variables, 2 texture coordinates, 4 bytes per float.
		
		//Creates Vertices FloatBuffer.
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexSize * maxAmountOfVertices); //Vertex size times maximum amount of vertices.
		byteBuffer.order(ByteOrder.nativeOrder());
		vertices = byteBuffer.asFloatBuffer();
		
		//Creates Indices ShortBuffer.
		if(maxAmountOfIndices > 0){ //Indices used.
			byteBuffer = ByteBuffer.allocateDirect(INDICES_SIZE * maxAmountOfIndices); //Indices size times maximum amount of indices.
			byteBuffer.order(ByteOrder.nativeOrder());
			indices = byteBuffer.asShortBuffer();
		}else{
			indices = null; //Indices not used.
		}
	}
	
	public void setVertices(float[] vertices, int offset, int length) {
		this.vertices.clear();
		this.vertices.put(vertices, offset, length);
		this.vertices.flip();
	}
	
	public void setIndices(short[] indices, int offset, int length) {
		this.indices.clear();
		this.indices.put(indices, offset, length);
		this.indices.flip();
	}
	
	public void bind() {
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		vertices.position(0);
		GLES10.glVertexPointer(2, GLES10.GL_FLOAT, vertexSize, vertices); //2 coordinates, uses floats, vertexSize, vertices array.
		
		if(hasColor){
			GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
			vertices.position(2); //Color coordinates starts at position 2.
			GLES10.glColorPointer(4, GLES10.GL_FLOAT, vertexSize, vertices); //4 color variables, uses floats, vertexSize, vertices array.
		}
		if(hasTexture){
			GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			vertices.position(hasColor?6:2); //If hasColor texture coordinates starts position 6, otherwise at position 2.
			GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, vertexSize, vertices); //2 texture coordinates, uses floats, vertexSize, vertices array.
		}
	}
	
	public void draw(int primitiveType, int offset, int verticesAmount) {
		if(indices != null){
			indices.position(offset);
			GLES10.glDrawElements(primitiveType, verticesAmount, GLES10.GL_UNSIGNED_SHORT, indices);
		}else{
			GLES10.glDrawArrays(primitiveType, offset, verticesAmount);
		}
	}
	
	public void unBind() {
		if(hasColor){
			GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		}
		if(hasTexture){
			GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
		}
	}
}
