package com.bumptech.glide.load.engine

import android.annotation.SuppressLint
import com.bumptech.glide.load.engine.executor.GlideExecutor
import java.lang.reflect.Field

internal var Engine.EngineJobFactory.animationExecutorHack: GlideExecutor
	@SuppressLint("VisibleForTests")
	get() = animationExecutor
	set(value) {
		logReplace(animationExecutorField, value)
		animationExecutorField.set(this, value)
	}

@delegate:SuppressLint("VisibleForTests")
private val animationExecutorField: Field by lazy {
	try {
		Engine.EngineJobFactory::class.java
			.getDeclaredField("animationExecutor")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide EngineJobFactory animationExecutor cannot be found", ex)
	}
}
