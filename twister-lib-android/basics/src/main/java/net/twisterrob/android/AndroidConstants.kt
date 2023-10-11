package net.twisterrob.android

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.annotation.AnyRes

object AndroidConstants {

	const val INVALID_POSITION: Int = -1

	@SuppressLint("InlinedApi")
	@AnyRes
	const val INVALID_RESOURCE_ID: Int = Resources.ID_NULL

	const val ANDROID_PACKAGE: String = "android"

	const val RES_TYPE_ID: String = "id"
	const val RES_TYPE_STRING: String = "string"
	const val RES_TYPE_RAW: String = "raw"
	const val RES_TYPE_DRAWABLE: String = "drawable"
	const val RES_TYPE_COLOR: String = "color"
	const val RES_TYPE_DIMEN: String = "dimen"
}
