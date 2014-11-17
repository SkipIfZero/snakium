package com.skipifzero.snakium.framework.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.SoundPool;

/**
 * A class representing a sound effect loaded from the Assets folder.
 * 
 * Should be created using AudioUtil since it should ideally share a SoundPool with other
 * SoundEffect's. This is why the constructor is protected.
 * 
 * @author Peter Hillerström
 * @since 2013-04-14
 * @version 1
 */
public class SoundEffect {
	
	/**
	 * The default volume. The scale is: 0.0f <= volume <= 1.0f.
	 */
	public static final float DEFAULT_VOLUME = 1.0f;
	
	private final String fileName;
	private final SoundPool soundPool;
	private int id;
	private float volume;
	
	/**
	 * Creates a new SoundEffect with the specified parameters
	 * @param fileName the path to the sound file in the assets folder
	 * @param soundPool the SoundPool
	 * @param assets the AssetManager
	 * @throws IllegalArgumentException if SoundEffect couldn't be loaded.
	 */
	protected SoundEffect(String fileName, SoundPool soundPool, AssetManager assets) {
		this.fileName = fileName;
		this.soundPool = soundPool;
		this.volume = DEFAULT_VOLUME;
		load(assets);
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Plays this SoundEffect once.
	 */
	public void play() {
		soundPool.play(id, volume, volume, 0, 0, 1); //Volume range. 0.0 <= volume <= 1.0
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets the volume of this SoundEffect.
	 * 0.0f <= volume <= 1.0f
	 * @param volume the new volume
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns the volume of this SoundEffect.
	 * @return volume of this SoundEffect
	 */
	public float getVolume() {
		return volume;
	}
	
	/*
	 * Lifecycle methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Loads this SoundEffect.
	 * @param assets the AssetManager
	 * @throws IllegalArgumentException if SoundEffect couldn't be loaded
	 */
	public void load(AssetManager assets) {
		try {
			AssetFileDescriptor assetFileDescriptor = assets.openFd(fileName);
			this.id = soundPool.load(assetFileDescriptor, 0);
		} catch (IOException e) {
			throw new IllegalArgumentException("Couldn't load SoundEffect from: " + fileName);
		}
	}
	
	/**
	 * Disposes of this SoundEffect. If it is disposed it needs to be loaded again before it can be
	 * used.
	 */
	public void dispose() {
		soundPool.unload(id);
	}
}
