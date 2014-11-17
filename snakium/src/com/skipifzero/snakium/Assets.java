package com.skipifzero.snakium;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.skipifzero.snakium.framework.audio.AudioUtil;
import com.skipifzero.snakium.framework.audio.Music;
import com.skipifzero.snakium.framework.audio.SoundEffect;
import com.skipifzero.snakium.framework.opengl.SpriteBatcher;
import com.skipifzero.snakium.framework.opengl.Texture;
import com.skipifzero.snakium.framework.opengl.TextureRegion;
import com.skipifzero.snakium.framework.opengl.TextureUtil;

public class Assets {
	
	private final AssetManager assetManager;
	private boolean isLoaded;
	
	//Private TextureUtils
	private final TextureUtil texUtil128, texUtil1024;
	
	//Textures
	public Texture texAtlas128, texAtlas1024;
	
	//TextureRegions
	//Miscellaneous
	public final TextureRegion touchStartRegion, touchCurrentRegion, touchLineRegion;
	//Snake
	public final TextureRegion 	HEAD_D2U_F1, HEAD_D2U_F2, HEAD_D2U_F3, HEAD_D2U_DIG_F3, HEAD_D2R_F3, HEAD_D2R_DIG_F3,
								DEADHEAD_D2U_F3, DEADHEAD_D2U_DIG_F3, DEADHEAD_D2R_F3, DEADHEAD_D2R_DIG_F3,
								BODY_D2U, BODY_D2U_DIG, BODY_D2R, BODY_D2R_DIG,
								TAIL_D2U_F1, TAIL_D2U_F2, TAIL_D2U_DIG_F1, TAIL_D2U_DIG_F2, TAIL_D2R_F1, TAIL_D2R_F2, TAIL_D2R_DIG_F1, TAIL_D2R_DIG_F2;
	
	//TouchButton
	public final TextureRegion 	BUTTON_LEFT, BUTTON_LEFT_TOUCHED, BUTTON_LEFT_DISABLED, 
								BUTTON_MIDDLE_TOUCHED, 
								BUTTON_RIGHT, BUTTON_RIGHT_TOUCHED, BUTTON_RIGHT_DISABLED;
	
	//Misc
	public final TextureRegion	OBJECT, BONUS_OBJECT, FILLED, TILE_BORDER;
	
	
	//1024 pixels:
	public final TextureRegion SNAKIUM_LOGO, SKIPIFZERO_SNAKIUM_LOGO, SKIPIFZERO_LOGO, COFFER_LOGO;

	//FontRenderers
	public final BitmapFontRenderer font;
	
	//SpriteBatcher
	public final SpriteBatcher batcher;
	
	//AudioUtil
	private final AudioUtil audioUtil;
	
	//Music
	public final Music	MENU_MUSIC, GAME_MUSIC;
	
	//SoundEffects
	public final SoundEffect	OBJECT_AQUIRED_SND, BONUS_OBJECT_AQUIRED_SND, BONUS_STARTED_SND, 
								BONUS_FAILED_SND, SNAKE_TURN_SND, WALL_CROSS_SND, GAME_OVER_SND;
	
	public Assets(AssetManager assetManager) {
		this.assetManager = assetManager;
		this.isLoaded = true; //Should be loaded after constructor.
		
		//TextureUtils
		this.texUtil128 = new TextureUtil("128pix", Bitmap.Config.ARGB_8888).load(assetManager);
		this.texUtil1024 = new TextureUtil("1024pix", Bitmap.Config.ARGB_8888).load(assetManager);
		
		//Textures
		this.texAtlas128 = texUtil128.getTextureAtlas(); 
		this.texAtlas1024 = texUtil1024.getTextureAtlas();
		
		//TextureRegion
		//Snake
		HEAD_D2U_F1 = texUtil128.getTextureRegion("head_d2u_f1_128.png");
		HEAD_D2U_F2 = texUtil128.getTextureRegion("head_d2u_f2_128.png");
		HEAD_D2U_F3 = texUtil128.getTextureRegion("head_d2u_f3_128.png");
		HEAD_D2U_DIG_F3 = texUtil128.getTextureRegion("head_d2u_dig_f3_128.png");
		HEAD_D2R_F3 = texUtil128.getTextureRegion("head_d2r_f3_128.png");
		HEAD_D2R_DIG_F3 = texUtil128.getTextureRegion("head_d2r_dig_f3_128.png");
		DEADHEAD_D2U_F3 = texUtil128.getTextureRegion("deadhead_d2u_f3_128.png");
		DEADHEAD_D2U_DIG_F3 = texUtil128.getTextureRegion("deadhead_d2u_dig_f3_128.png");
		DEADHEAD_D2R_F3 = texUtil128.getTextureRegion("deadhead_d2r_f3_128.png");
		DEADHEAD_D2R_DIG_F3 = texUtil128.getTextureRegion("deadhead_d2r_dig_f3_128.png");
		BODY_D2U = texUtil128.getTextureRegion("body_d2u_128.png");
		BODY_D2U_DIG = texUtil128.getTextureRegion("body_d2u_dig_128.png");
		BODY_D2R = texUtil128.getTextureRegion("body_d2r_128.png");
		BODY_D2R_DIG = texUtil128.getTextureRegion("body_d2r_dig_128.png");
		TAIL_D2U_F1 = texUtil128.getTextureRegion("tail_d2u_f1_128.png");
		TAIL_D2U_F2 = texUtil128.getTextureRegion("tail_d2u_f2_128.png");
		TAIL_D2U_DIG_F1 = texUtil128.getTextureRegion("tail_d2u_dig_f1_128.png");
		TAIL_D2U_DIG_F2 = texUtil128.getTextureRegion("tail_d2u_dig_f2_128.png");
		TAIL_D2R_F1 = texUtil128.getTextureRegion("tail_d2r_f1_128.png");
		TAIL_D2R_F2 = texUtil128.getTextureRegion("tail_d2r_f2_128.png");
		TAIL_D2R_DIG_F1 = texUtil128.getTextureRegion("tail_d2r_dig_f1_128.png");
		TAIL_D2R_DIG_F2 = texUtil128.getTextureRegion("tail_d2r_dig_f2_128.png");
		//Button Regions
		BUTTON_LEFT = texUtil128.getTextureRegion("button_left_128.png");
		BUTTON_LEFT_TOUCHED = texUtil128.getTextureRegion("button_left_touched_128.png");
		BUTTON_LEFT_DISABLED = texUtil128.getTextureRegion("button_left_disabled_128.png");
		BUTTON_MIDDLE_TOUCHED = texUtil128.getTextureRegion("button_middle_touched_128.png");
		BUTTON_RIGHT = texUtil128.getTextureRegion("button_right_128.png");
		BUTTON_RIGHT_TOUCHED = texUtil128.getTextureRegion("button_right_touched_128.png");
		BUTTON_RIGHT_DISABLED = texUtil128.getTextureRegion("button_right_disabled_128.png");
		//Misc
		OBJECT = texUtil128.getTextureRegion("object_128.png");
		BONUS_OBJECT = texUtil128.getTextureRegion("bonus_object_128.png");
		FILLED = texUtil128.getTextureRegion("filled_64.png");
		TILE_BORDER = texUtil128.getTextureRegion("tile_border_64.png");
		
		//Misc
		touchStartRegion = texUtil128.getTextureRegion("touch_start_128.png");
		touchCurrentRegion = texUtil128.getTextureRegion("touch_current_128.png");
		touchLineRegion = texUtil128.getTextureRegion("touch_line_128.png");
		
		//1024 pixels
		SNAKIUM_LOGO = texUtil1024.getTextureRegion("snakium_ascii_logo_1024x256.png");
		SKIPIFZERO_SNAKIUM_LOGO = texUtil1024.getTextureRegion("skipifzero_snakium_logo_1024x256.png");
		SKIPIFZERO_LOGO = texUtil1024.getTextureRegion("skipifzero_logo_1024x256.png");
		COFFER_LOGO = texUtil1024.getTextureRegion("coffer_logo_1024x256.png");
		
		//FontRenderers
//		new BitmapFontGenerator()
//			.setFont(Typeface.createFromAsset(assetManager, "Inconsolata.otf"))
//			.setSize(100)
//			.setXPadding(13)
//			.setYPadding(13)
//			.setColor(Color.argb(255, 215, 255, 215))
//			.build("snakium/fontNEW.cfg", "snakium/fontNEW.png");
		
		long startTime = System.nanoTime();
		this.font = new BitmapFontRenderer(assetManager, "font.png", "font.cfg");
		Log.d("Assets", "BitmapFontRenderer creation time: " + ((System.nanoTime() - startTime) / 1000000000.0));
		
		//SpriteBatcher
		this.batcher = new SpriteBatcher(6000);
		
		//AudioUtil
		this.audioUtil = new AudioUtil(assetManager);
		
		//Music
		MENU_MUSIC = null;
		//MENU_MUSIC.setLooping(true);
		GAME_MUSIC = audioUtil.getMusic("audio/music/full2.mp3");
		GAME_MUSIC.setLooping(true);
		
		//SoundEffects
		float volume = 1f;
		OBJECT_AQUIRED_SND = audioUtil.getSoundEffect("audio/sfx/plipp.mp3");
		OBJECT_AQUIRED_SND.setVolume(volume);
		BONUS_OBJECT_AQUIRED_SND = audioUtil.getSoundEffect("audio/sfx/4.mp3");
		BONUS_OBJECT_AQUIRED_SND.setVolume(volume);
		BONUS_STARTED_SND = audioUtil.getSoundEffect("audio/sfx/6.mp3");
		BONUS_STARTED_SND.setVolume(volume);
		BONUS_FAILED_SND = audioUtil.getSoundEffect("audio/sfx/7v2.mp3");
		BONUS_FAILED_SND.setVolume(volume);
		SNAKE_TURN_SND = null;
		WALL_CROSS_SND = null;
		GAME_OVER_SND = audioUtil.getSoundEffect("audio/sfx/2loud.mp3");
		GAME_OVER_SND.setVolume(0.9f);
	}
	
	/**
	 * Reloads all assets that need to be reloaded. Should generally be called in GLScreen's
	 * onResume() method.
	 */
	public void reload() {
		if(!isLoaded) {
			this.isLoaded = true; //Assets are now loaded.
		
			//TextureUtils
			texUtil128.load(assetManager);
			texUtil1024.load(assetManager);
			
			//Textures
			texAtlas128 = texUtil128.getTextureAtlas();
			texAtlas1024 = texUtil1024.getTextureAtlas();
			
			//FontRenderers
			font.reload();
			font.reload();
			
			//Audio
			audioUtil.reload(); //TODO: I don't think you actually need to reload Audio assets.
		}
	}
	
	/**
	 * Pauses all assets that need to be paused (for example music). Should generally be called in
	 * GLScreen's onPause() method.
	 */
	public void pause() {
		this.isLoaded = false; //Assets are no longer loaded.
		
		audioUtil.onPause();
	}
	
	/**
	 * Disposes of all assets that need to be disposed of.
	 */
	public void dispose() {
		this.isLoaded = false; //Assets are no longer loaded.
		
		texUtil128.dispose();
		texUtil1024.dispose();
		
		font.dispose();
		font.dispose();
		
		audioUtil.dispose();
	}
	
	/**
	 * Returns whether the assets are loaded or not. If they are not they need to be loaded via
	 * reload().
	 * @return whether assets are loaded or not
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
}
