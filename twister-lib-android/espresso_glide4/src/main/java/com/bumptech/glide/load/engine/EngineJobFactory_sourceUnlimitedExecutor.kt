package com.bumptech.glide.load.engine

import android.annotation.SuppressLint
import com.bumptech.glide.load.engine.executor.GlideExecutor
import java.lang.reflect.Field

internal var Engine.EngineJobFactory.sourceUnlimitedExecutorHack: GlideExecutor
	@SuppressLint("VisibleForTests")
	get() = sourceUnlimitedExecutor
	set(value) {
		logReplace(sourceUnlimitedExecutorField, value)
		sourceUnlimitedExecutorField.set(this, value)
	}

@delegate:SuppressLint("VisibleForTests")
private val sourceUnlimitedExecutorField: Field by lazy {
	try {
		Engine.EngineJobFactory::class.java
			.getDeclaredField("sourceUnlimitedExecutor")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException(
			"Glide EngineJobFactory sourceUnlimitedExecutor cannot be found",
			ex
		)
	}
}
