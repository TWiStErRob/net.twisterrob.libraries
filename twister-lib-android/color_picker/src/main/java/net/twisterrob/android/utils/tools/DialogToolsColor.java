package net.twisterrob.android.utils.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.rarepebble.colorpicker.ColorPickerView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import net.twisterrob.android.utils.tools.DialogTools.PopupCallbacks;

@SuppressWarnings("unused")
public class DialogToolsColor {

	@TargetApi(VERSION_CODES.HONEYCOMB) // ColorPickerView (1.7.0) requires API 11 minimum
	public static AlertDialog.Builder pickColor(
			@NonNull Context context,
			@ColorInt int initial,
			final @NonNull PopupCallbacks<Integer> callbacks
	) {
		if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
			final ColorPickerView picker = new ColorPickerView(context);
			picker.setColor(initial);
			return new AlertDialog.Builder(context)
					.setView(picker)
					.setPositiveButton(android.R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							callbacks.finished(picker.getColor());
						}
					})
					.setNegativeButton(android.R.string.cancel, new OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							callbacks.finished(null);
						}
					})
					.setTitle("Pick a color");
		} else {
			return DialogTools
					.prompt(context, Integer.toHexString(initial), new PopupCallbacks<String>() {
						@Override public void finished(String value) {
							try {
								callbacks.finished(Integer.parseInt(value, 16));
							} catch (NumberFormatException ex) {
								callbacks.finished(null);
							}
						}
					})
					.setTitle("Pick a color");
		}
	}
}
