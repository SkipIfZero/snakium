package com.skipifzero.snakium.framework.entities;

import android.graphics.Color;

import com.skipifzero.snakium.framework.math.BaseVector2;
import com.skipifzero.snakium.framework.math.Vector2;

/**
 * An implementation of the TextEntity interface. Used to create a TextEntity that floats in a 
 * specified direction on the screen while fading away.
 * 
 * Designed in such a way that it can easily be reused or "pooled" to avoid making the garbage
 * collector angry.
 * 
 * @author Peter Hillerström
 * @since 2013-04-10
 * @version 2
 */
public class FloatingTextEntity implements TextEntity {

	private String text;
	private final Vector2 position = new Vector2(0,0);
	private final Vector2 velocity = new Vector2(0,0);
	private int color;
	private double size, angle, opacity;
	private double duration, timeLeft;
	boolean active;
	
	//Temp variables
	private final Vector2 temp = new Vector2(0,0);
	
	/*
	 * Constructor
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Creates a new FloatingTextEntity, but it needs to be set before it can be used.
	 */
	public FloatingTextEntity() {
		active = false;
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns whether this FloatingTextEntity is active or not.
	 * @return whether this FloatingTextEntity is active or not
	 */
	public boolean isActive() {
		return active;
	}
	
	/*
	 * Setters (Semi-constructors)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets this FloatingTextEntity to the value of the specified TextEntity and other parameters.
	 * @param textEntity the TextEntity
	 * @param velocity the velocity vector
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(TextEntity textEntity, BaseVector2 velocity, double duration) {
		this.text = textEntity.getText();
		this.position.set(textEntity.getPosition());
		this.velocity.set(velocity);
		this.color = textEntity.getColor();
		this.size = textEntity.getSize();
		this.angle = textEntity.getAngle();
		this.opacity = 1.0;
		this.duration = duration;
		this.timeLeft = duration;
		this.active = true;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the value of the specified TextEntity and other parameters.
	 * @param textEntity the TextEntity
	 * @param velX the velocity x-coordinate
	 * @param velY the velocity y-coordinate
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(TextEntity textEntity, double velX, double velY, double duration) {
		this.text = textEntity.getText();
		this.position.set(textEntity.getPosition());
		this.velocity.set(velocity);
		this.color = textEntity.getColor();
		this.size = textEntity.getSize();
		this.angle = textEntity.getAngle();
		this.opacity = 1.0;
		this.duration = duration;
		this.timeLeft = duration;
		this.active = true;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the value of the specified FloatingTextEntity.
	 * @param floatingTextEntity
	 * @return this
	 */
	public FloatingTextEntity set(FloatingTextEntity floatingTextEntity) {
		this.text = floatingTextEntity.text;
		this.position.set(floatingTextEntity.position);
		this.velocity.set(floatingTextEntity.velocity);
		this.color = floatingTextEntity.color;
		this.size = floatingTextEntity.size;
		this.angle = floatingTextEntity.angle;
		this.opacity = floatingTextEntity.opacity;
		this.duration = floatingTextEntity.duration;
		this.timeLeft = floatingTextEntity.timeLeft;
		this.active = floatingTextEntity.active;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings.
	 * @param text the text
	 * @param position the position
	 * @param velocity the velocity vector
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param angle the angle of the text
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(String text, BaseVector2 position, BaseVector2 velocity, int color, double size, double angle, double duration) {
		this.text = text;
		this.position.set(position);
		this.velocity.set(velocity);
		this.color = color;
		this.size = size;
		this.angle = angle;
		this.opacity = 1.0;
		this.duration = duration;
		this.timeLeft = duration;
		this.active = true;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings.
	 * @param text the text
	 * @param posX the position x-coordinate
	 * @param posY the position y-coordinate
	 * @param velX the velocity x-coordinate
	 * @param velY the velocity y-coordinate
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param angle the angle of the text
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(String text, double posX, double posY, double velX, double velY, int color, double size, double angle, double duration) {
		this.text = text;
		this.position.set(posX, posY);
		this.velocity.set(velX, velY);
		this.color = color;
		this.size = size;
		this.angle = angle;
		this.opacity = 1.0;
		this.duration = duration;
		this.timeLeft = duration;
		this.active = true;
		return this;
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings. Angle will be set to 0 degrees.
	 * @param text the text
	 * @param position the position
	 * @param velocity the velocity vector
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(String text, BaseVector2 position, BaseVector2 velocity, int color, double size, double duration) {
		return set(text, position, velocity, color, size, 0, duration);
	}
	
	/**
	 * Sets this FloatingTextEntity to the specified settings. Angle will be set to 0 degrees.
	 * @param text the text
	 * @param posX the position x-coordinate
	 * @param posY the position y-coordinate
	 * @param velX the velocity x-coordinate
	 * @param velY the velocity y-coordinate
	 * @param color the color of the text
	 * @param size the size of the text
	 * @param duration the duration to display the text
	 * @return this
	 */
	public FloatingTextEntity set(String text, double posX, double posY, double velX, double velY, int color, double size, double duration) {
		return set(text, posX, posY, velX, velY, color, size, 0, duration);
	}
	
	/*
	 * Methods inherited from TextEntity
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void update(double deltaTime) {
		position.add(temp.set(velocity).mult(deltaTime)); //Updates position of text.
		timeLeft -= deltaTime;
		if(timeLeft <= 0) {
			active = false;
			opacity = 0;
			color = 0; // == Color.argb(0, 0, 0, 0);
		} else {
			opacity = timeLeft/duration;
			color = Color.argb((int)(opacity*255), Color.red(color), Color.green(color), Color.blue(color));
		}
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
		return opacity;
	}
	
	@Override
	public int getColor() {
		return color;
	}
}
