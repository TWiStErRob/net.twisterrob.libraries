package net.twisterrob.android;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import androidx.annotation.AnyRes;

public class AndroidConstants {
	public static final int INVALID_POSITION = -1;
	@SuppressLint("InlinedApi")
	public static final @AnyRes int INVALID_RESOURCE_ID = Resources.ID_NULL;
	public static final String ANDROID_PACKAGE = "android";
	public static final String RES_TYPE_ID = "id";
	public static final String RES_TYPE_STRING = "string";
	public static final String RES_TYPE_RAW = "raw";
	public static final String RES_TYPE_DRAWABLE = "drawable";
	public static final String RES_TYPE_COLOR = "color";
	public static final String RES_TYPE_DIMEN = "dimen";
}
