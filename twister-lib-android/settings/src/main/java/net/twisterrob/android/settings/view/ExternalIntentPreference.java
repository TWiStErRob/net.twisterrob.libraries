package net.twisterrob.android.settings.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;

import androidx.preference.Preference;

import net.twisterrob.android.utils.tools.PackageManagerTools;

/**
 * Simple stand-in for {@link Preference} which only has an {@code <intent>} in it.
 * Instead of crashing, automatically disable the click-ability
 * when the {@link android.content.Intent} is not resolvable.
 * <p>
 * <b>WARNING</b>: On API 29 and higher this requires {@code <queries>} in the manifest.
 * </p>
 */
public class ExternalIntentPreference extends Preference {

	public ExternalIntentPreference(Context context) {
		super(context);
	}
	public ExternalIntentPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ExternalIntentPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	public ExternalIntentPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override public void onAttached() {
		super.onAttached();
		PackageManager pm = getContext().getPackageManager();
		@SuppressLint("MissingPermission") // Requires <queries> in manifest.
		List<ResolveInfo> intents = PackageManagerTools.queryIntentActivities(pm, getIntent(), 0);
		setEnabled(!intents.isEmpty());
	}
}
