package com.skipifzero.snakium.editor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.skipifzero.snakium.CustomConfig;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.model.SnakiumConfigBuilder;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;
import com.skipifzero.snakium.screens.GameScreen;

public class DefaultsDialogFragment extends DialogFragment {

	private int config_id = -1;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] items = {"snakium", "snake 2", "classic"};
		
		BoundingRectangle bounds = GameScreen.getGameBoardMaxBounds();
		final SnakiumConfig[] configs = new SnakiumConfig[3];
		if(bounds == null) {
			configs[0] = SnakiumConfigBuilder.SNAKIUM_BASE_CONFIG;
			configs[1] = SnakiumConfigBuilder.SNAKE2_BASE_CONFIG;
			configs[2] = SnakiumConfigBuilder.SNAKIUM_BASE_CONFIG;
		} else if(bounds.getWidth() <= bounds.getHeight()) {
			configs[0] = SnakiumConfigBuilder.SNAKIUM_HIGH_CONFIG;
			configs[1] = SnakiumConfigBuilder.SNAKE2_HIGH_CONFIG;
			configs[2] = SnakiumConfigBuilder.SNAKIUM_HIGH_CONFIG;
		} else {
			configs[0] = SnakiumConfigBuilder.SNAKIUM_WIDE_CONFIG;
			configs[1] = SnakiumConfigBuilder.SNAKE2_WIDE_CONFIG;
			configs[2] = SnakiumConfigBuilder.SNAKIUM_WIDE_CONFIG;
		}
		
		return new AlertDialog.Builder(getActivity())
			.setTitle("Restore to defaults")
			
			.setSingleChoiceItems(items, -1, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					config_id = which;
				}
			})
			
			.setPositiveButton("Apply", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(0 <= config_id && config_id < configs.length) {
						CustomConfig.set(configs[config_id]);
					}
				}
			})
			.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Do nothing.
				}
			})
			
			.create();
	}
}
