package com.skipifzero.snakium.editor;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.skipifzero.snakium.CustomConfig;
import com.skipifzero.snakium.R;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.screens.GameScreen;

public class BoardSizeDialogFragment extends DialogFragment {
	
	private EditText widthInput, heightInput;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.board_size, null);
		
		this.widthInput = (EditText)view.findViewById(R.id.widthInput);
		widthInput.setText(Integer.toString(CustomConfig.boardWidth()));
		
		this.heightInput = (EditText)view.findViewById(R.id.heightInput);
		heightInput.setText(Integer.toString(CustomConfig.boardHeight()));
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
			.setTitle("Board size")
			
			.setView(view)
			
			.setPositiveButton("Apply", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						int w = Integer.parseInt(widthInput.getText().toString());
						CustomConfig.boardWidth(w);
					} catch (NumberFormatException e) {
						//Do nothing.
					}
					try {
						int h = Integer.parseInt(heightInput.getText().toString());
						CustomConfig.boardHeight(h);
					} catch (NumberFormatException e) {
						//Do nothing.
					}
				}
			});
		
		if(GameScreen.getGameBoardMaxBounds() != null) {
			dialogBuilder
				.setNeutralButton("Optimal", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BoundingRectangle bounds = GameScreen.getGameBoardMaxBounds();
						
						int w, h;
						if(bounds.getWidth() <= bounds.getHeight()) {
							w = 12;
							h = (int)Math.round((bounds.getHeight()*w)/bounds.getWidth());
						} else {
							h = 12;
							w = (int)Math.round((bounds.getWidth()*h)/bounds.getHeight());
						}
						
						CustomConfig.boardWidth(w);
						CustomConfig.boardHeight(h);
					}
				});
		}
		
		dialogBuilder
			.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Do nothing.
				}
			});
		
		return dialogBuilder.create();
	}
}
