@file:JvmName("StrictModeTools")

package net.twisterrob.android.utils.tools

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.StrictMode

fun StrictMode.ThreadPolicy.builder(): StrictMode.ThreadPolicy.Builder =
	StrictMode.ThreadPolicy.Builder(this)

fun StrictMode.VmPolicy.builder(): StrictMode.VmPolicy.Builder =
	StrictMode.VmPolicy.Builder(this)

inline fun <R> allowThreadDiskReads(block: () -> R): R {
	val policy = StrictMode.allowThreadDiskReads()
	try {
		return block()
	} finally {
		StrictMode.setThreadPolicy(policy)
	}
}

inline fun <R> allowThreadDiskWrites(block: () -> R): R {
	val policy = StrictMode.allowThreadDiskWrites()
	try {
		return block()
	} finally {
		StrictMode.setThreadPolicy(policy)
	}
}

inline fun <R> allowNonSdkApiUsage(block: () -> R): R {
	val policy = StrictMode.getVmPolicy()
	try {
		if (VERSION.SDK_INT >= VERSION_CODES.P) {
			StrictMode.setVmPolicy(policy.builder().permitNonSdkApiUsage().build())
		}
		return block()
	} finally {
		StrictMode.setVmPolicy(policy)
	}
}

inline fun <R> allowUnsafeIntentLaunch(block: () -> R): R {
	val policy = StrictMode.getVmPolicy()
	try {
		if (VERSION.SDK_INT >= VERSION_CODES.S) {
			StrictMode.setVmPolicy(policy.builder().permitUnsafeIntentLaunch().build())
		}
		return block()
	} finally {
		StrictMode.setVmPolicy(policy)
	}
}
