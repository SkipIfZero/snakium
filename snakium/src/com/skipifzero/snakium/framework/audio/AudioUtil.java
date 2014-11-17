package com.skipifzero.snakium.framework.audio;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Class for managing and creating Music and SoundEffects.
 * @author Peter Hillerström
 * @since 2013-04-14
 * @version 1
 */
public class AudioUtil {
	
	private final AssetManager assets;
	private final SoundPool soundPool;
	private final Map<String, SoundEffect> soundEffects;
	private final Map<String, Music> musics;
	
	/**
	 * Creates new AudioUtil.
	 * @param assets the AssetManager
	 */
	public AudioUtil(AssetManager assets) {
		this.assets = assets;
		this.soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
		this.soundEffects = new HashMap<String, SoundEffect>();
		this.musics = new HashMap<String, Music>();
	}
	
	/*
	 * Creation methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Gets the SoundEffect at the specified path in assets folder.
	 * @param fileName the specified path in assets folder
	 * @return SoundEffect
	 */
	public SoundEffect getSoundEffect(String fileName) {
		if(soundEffects.containsKey(fileName)) {
			return soundEffects.get(fileName);
		}
		
		SoundEffect soundEffect = new SoundEffect(fileName, soundPool, assets);
		soundEffects.put(fileName, soundEffect);
		return soundEffect;
	}
	
	/**
	 * Gets the Music at the specified path in assets folder.
	 * @param fileName the specified path in assets folder
	 * @return Music
	 */
	public Music getMusic(String fileName) {
		if(musics.containsKey(fileName)) {
			return musics.get(fileName);
		}
		
		Music music = new Music(fileName, assets);
		musics.put(fileName, music);
		return music;
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns a Collection of all SoundEffect's created by this AudioUtil.
	 * @return Collection of SoundEffects
	 */
	public Collection<SoundEffect> getSoundEffects() {
		return soundEffects.values();
	}
	
	/**
	 * Returns a Collection of all Music's created by this AudioUtil.
	 * @return Collection of Musics
	 */
	public Collection<Music> getMusics() {
		return musics.values();
	}
	
	/*
	 * Lifecycle methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Reloads all SoundEffects and Musics created by this AudioUtil.
	 * 
	 * Currently unsure when this should be called as it seems you don't have to reload Music and
	 * SoundEffects after you've paused the application. More research needed.
	 */
	public void reload() {
		for(SoundEffect soundEffect : soundEffects.values()) {
			soundEffect.load(assets);
		}
		for(Music music : musics.values()) {
			music.load(assets);
		}
	}
	
	/**
	 * Pauses all Music created by this AudioUtil.
	 */
	public void onPause() {
		for(Music music : musics.values()) {
			music.pause();
		}
	}
	
	/**
	 * Disposes of all SoundEffects and Musics created by this AudioUtil.
	 */
	public void dispose() {
		for(SoundEffect soundEffect : soundEffects.values()) {
			soundEffect.dispose();
		}
		for(Music music : musics.values()) {
			music.dispose();
		}
	}
}
