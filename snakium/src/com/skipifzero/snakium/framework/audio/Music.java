package com.skipifzero.snakium.framework.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * A class representing a music file loaded from the Assets folder.
 * 
 * Should be created using AudioUtil, this is why the constructor is protected.
 * 
 * @author Peter Hillerström
 * @since 2013-04-14
 * @version 1
 */
public class Music {
	
	/**
	 * The default volume.
	 */
	public static final float DEFAULT_VOLUME = 1.0f;
	
	private final String fileName;
	private MediaPlayer mediaPlayer;
	private boolean isPrepared;
	private final CompletionListener completionListener;
	private float volume;
	
	/**
	 * Creates a new Music instance with the specified parameters.
	 * @param fileName the path to the music file in the assets folder
	 * @param assets the AssetManager
	 */
	protected Music(String fileName, AssetManager assets) {
		this.fileName = fileName;
		this.completionListener = new CompletionListener(this);
		this.isPrepared = false;
		this.volume = DEFAULT_VOLUME;
		load(assets);
	}

	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Resumes playback of this Music.
	 */
	public void play() {
		if(mediaPlayer.isPlaying()) {
			return;
		}
		
		try {
			synchronized(this) {
				if(!isPrepared) {
					mediaPlayer.prepare();
				}
				mediaPlayer.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pauses playback of this Music.
	 */
	public void pause() {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}
	
	/**
	 * Stops playback of this Music.
	 */
	public void stop() {
		mediaPlayer.stop();
		synchronized(this) {
			isPrepared = false;
		}
	}
	
	/**
	 * Returns the current position of the playback in milliseconds.
	 * @return current position of the playback in milliseconds.
	 */
	public int getPosition() {
		return mediaPlayer.getCurrentPosition();
	}
	
	/**
	 * Sets the current position of the playback.
	 * @param msec the requested position in milliseconds
	 */
	public void setPosition(int msec) {
		mediaPlayer.seekTo(msec);
	}
	
	/*
	 * Getters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns the volume of this Music.
	 * @return volume of this Music
	 */
	public float getVolume() {
		return volume;
	}
	
	/**
	 * Returns whether this Music is set to loop or not.
	 * @return whether this Music is set to loop or not
	 */
	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}
	
	/**
	 * Returns whether this Music is currently playing or not.
	 * @return whether this Music is currently playing or not
	 */
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	/**
	 * Returns whether this Music is stopped or not.
	 * @return whether this Music is stopped or not
	 */
	public boolean isStopped() {
		return !isPrepared; //If the MediaPlayer isn't prepared it's stopped.
	}
	
	/*
	 * Setters
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Sets the volume of this Music.
	 * @param volume the volume to set
	 */
	public void setVolume(float volume) {
		this.volume = volume;
		this.mediaPlayer.setVolume(volume, volume);
	}
	
	/**
	 * Sets whether this Music should loop or not.
	 * @param isLooping whether this Music should loop or not
	 */
	public void setLooping(boolean isLooping) {
		mediaPlayer.setLooping(isLooping);
	}
	
	/*
	 * Lifecycle methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Loads this Music.
	 * @param assets the AssetManager
	 * @throws IllegalArgumentException if Music couldn't be loaded
	 * @throws RuntimeException if Music couldn't be loaded due to other (non-IOException) error
	 */
	public void load(AssetManager assets) {
		this.mediaPlayer = new MediaPlayer();
		try {
			AssetFileDescriptor assetFileDescriptor = assets.openFd(fileName);
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(completionListener);
		} catch(IOException e) {
			throw new IllegalArgumentException("Couldn't load Music from: " + fileName);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't load music (non-IOException). FileName: " + fileName);
		}
	}
	
	/**
	 * Disposes of this Music. If it is disposed it needs to be loaded again before it can be used.
	 */
	public void dispose() {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
	}
	
	/*
	 * Private CompletionListener class
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Private class to set preparation status after Music has finished playing.
	 */
	private class CompletionListener implements OnCompletionListener {
		
		private final Music lock;
		
		/**
		 * Creates a CompletionListener with the specified lock object.
		 * @param lock the Music instance owning this CompletionListener
		 */
		public CompletionListener(Music lock) {
			this.lock = lock;
		}
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			synchronized(lock) {
				isPrepared = false;
			}
		}
	}
}
