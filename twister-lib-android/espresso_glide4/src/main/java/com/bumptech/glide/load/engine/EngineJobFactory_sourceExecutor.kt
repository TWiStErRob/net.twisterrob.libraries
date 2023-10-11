package com.bumptech.glide.load.engine

import android.annotation.SuppressLint
import com.bumptech.glide.load.engine.executor.GlideExecutor
import java.lang.reflect.Field

internal var Engine.EngineJobFactory.sourceExecutorHack: GlideExecutor
	@SuppressLint("VisibleForTests")
	get() = sourceExecutor
	set(value) {
		logReplace(sourceExecutorField, value)
		sourceExecutorField.set(this, value)
	}

@delegate:SuppressLint("VisibleForTests")
private val sourceExecutorField: Field by lazy {
	try {
		Engine.EngineJobFactory::class.java
			.getDeclaredField("sourceExecutor")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide EngineJobFactory sourceExecutor cannot be found", ex)
	}
}
