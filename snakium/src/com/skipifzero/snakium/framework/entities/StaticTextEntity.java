package com.skipifzero.snakium.framework.entities;

import android.graphics.Color;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * An implementation of the TextEntity interface. Used to create a static TextEntity that doesn't
 * move around or change unless you manually change its value.
 * 
 * Designed in such a way that it can easily be reused or "pooled" to avoid making the garbage
 * collector angry.
 * 
 * @author Peter Hillerström
 * @since 2013-04-10
 * @version 1
 */
public class StaticTextEntity implements TextEntity {

	private String text;
	private final Vector2 position = new Vector2(0,0);
	private int color;
	private double size, angle;
	
	/*
	 * Constructor
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Creates a new StaticTextEntity, but it needs to be set before it can be used.
	 */
	public StaticTextEntity() {
		//Do nothing since everything is set in the set() methods.
	}
	
	/*
	 * Setters (Semi-constructors)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets this StaticTextEntity to the values of the specified TextEntity.
	 * @param textEntity the specified TextEntity
	 * @return this
	 */
	public StaticTextEntity set(TextEntity textEntity) {
		this.text = textEntity.getText();
		this.position.set(textEntity.getPosition());
		this.color = textEntity.getColor();
		this.size = textEntity.getSize();
		this.angle = textEntity.getAngle();
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings.
	 * @param text the text
	 * @param position the position
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param angle the angle of the text
	 * @return this
	 */
	public StaticTextEntity set(String text, BaseVector2 position, int color, double size, double angle) {
		this.text = text;
		this.position.set(position);
		this.color = color;
		this.size = size;
		this.angle = angle;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings.
	 * @param text the text
	 * @param posX the position x-coordinate
	 * @param posY the position y-coordinate
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param angle the angle of the text
	 * @return this
	 */
	public StaticTextEntity set(String text, double posX, double posY, int color, double size, double angle) {
		this.text = text;
		this.position.set(posX, posY);
		this.color = color;
		this.size = size;
		this.angle = angle;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings. Angle will be set to 0 degrees.
	 * @param text the text
	 * @param position the position
	 * @param color the color of the text
	 * @param size the size of the text
	 * @return this
	 */
	public StaticTextEntity set(String text, BaseVector2 position, int color, double size) {
		return set(text, position, color, size, 0);
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings. Angle will be set to 0 degrees.
	 * @param text the text
	 * @param posX the position x-coordinate
	 * @param posY the position y-coordinate
	 * @param color the color of the text
	 * @param size the size of the text
	 * @return this
	 */
	public StaticTextEntity set(String text, double posX, double posY, int color, double size) {
		return set(text, posX, posY, color, size, 0);
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets this StaticTextEntity's text to the specified text.
	 * @param text the specified text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Sets this StaticTextEntity's position the the specified position.
	 * @param position the specified position
	 */
	public void setPosition(BaseVector2 position) {
		this.position.set(position);
	}
	
	/**
	 * Sets this StaticTextEntity's position to the specified position.
	 * @param posX the specified positions x-coordinate
	 * @param posY the specified positions y-coordinate
	 */
	public void setPosition(double posX, double posY) {
		this.position.set(posX, posY);
	}
	
	/**
	 * Sets this StaticTextEntity's color to the specified color.
	 * @param color the specified color
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * Sets this StaticTextEntity's size to the specified size.
	 * @param size the specified size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * Sets this StaticTextEntity's angle to the specified angle.
	 * @param angle the specified angle
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	/*
	 * Methods inherited from TextEntity
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime) {
		//Do nothing since this is a static text.
	}

	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public BaseVector2 getPosition() {
		return position;
	}
	
	@Override
	public double getSize() {
		return size;
	}
	
	@Override
	public double getAngle() {
		return angle;
	}
	
	@Override
	public double getOpacity() {
		return Color.alpha(color);
	}
	
	@Override
	public int getColor() {
		return color;
	}
}
