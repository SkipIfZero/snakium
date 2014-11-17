package com.skipifzero.snakium.editor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import com.skipifzero.snakium.CustomConfig;

public class ObjectValueDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final EditText editText = new EditText(getActivity());
		editText.setText(Integer.toString(CustomConfig.pointsPerObject()));
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		return new AlertDialog.Builder(getActivity())
			.setTitle("Object value")
			.setView(editText)
			.setPositiveButton("Apply", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						int i = Integer.parseInt(editText.getText().toString());
						CustomConfig.pointsPerObject(i);
					} catch (NumberFormatException e) {
						//Do nothing.
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
