package com.bumptech.glide.load.engine

import android.annotation.SuppressLint
import com.bumptech.glide.load.engine.executor.GlideExecutor
import java.lang.reflect.Field

internal var Engine.EngineJobFactory.diskCacheExecutorHack: GlideExecutor
	@SuppressLint("VisibleForTests")
	get() = diskCacheExecutor
	set(value) {
		logReplace(diskCacheExecutorField, value)
		diskCacheExecutorField.set(this, value)
	}

@delegate:SuppressLint("VisibleForTests")
private val diskCacheExecutorField: Field by lazy {
	try {
		Engine.EngineJobFactory::class.java
			.getDeclaredField("diskCacheExecutor")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide EngineJobFactory diskCacheExecutor cannot be found", ex)
	}
}
