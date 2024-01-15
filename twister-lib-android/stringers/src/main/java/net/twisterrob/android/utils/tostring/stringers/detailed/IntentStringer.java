package net.twisterrob.android.utils.tostring.stringers.detailed;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;

import net.twisterrob.android.annotation.IntentFlags;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

@DebugHelper
@TargetApi(VERSION_CODES.JELLY_BEAN)
public class IntentStringer<T extends Intent> extends Stringer<T> {
	@Override public String getType(T object) {
		return null;
	}
	@Override public void toString(@NonNull ToStringAppender append, T intent) {
		append.beginSizedList(intent, 10, false); // TODO figure out a better way
		extraProperties(append, intent);
		if (intent.getPackage() != null) {
			append.item("pkg", intent.getPackage());
		}
		if (intent.getComponent() != null) {
			append.item("cmp", intent.getComponent());
		}
		if (intent.getFlags() != 0) {
			//noinspection ResourceType TOFIX external annotations to Intent#getFlags?
			append.item("flg", IntentFlags.Converter.toString(intent.getFlags(), null));
		}

		if (intent.getData() != null) {
			append.item("dat", intent.getData());
		}
		if (intent.getType() != null) {
			append.item("typ", intent.getType());
		}

		if (intent.getAction() != null) {
			append.item("act", intent.getAction());
		}
		if (intent.getCategories() != null) {
			append.item("cat", intent.getCategories());
		}

		if (intent.getSourceBounds() != null) {
			append.item("bnds", intent.getSourceBounds());
		}

		if (intent.getExtras() != null) {
			append.item("xtra", intent.getExtras());
		}
		if (VERSION_CODES.JELLY_BEAN <= VERSION.SDK_INT) {
			if (intent.getClipData() != null) {
				append.item("clip", intent.getClipData());
			}
		}
		append.endSizedList();
	}

	protected void extraProperties(ToStringAppender append, T intent) {
		// Optional override.
	}
}
