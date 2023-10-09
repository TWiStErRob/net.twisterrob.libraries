package com.bumptech.glide.load.engine

import com.bumptech.glide.Glide
import com.bumptech.glide.util.Executors
import java.lang.reflect.Field
import java.util.concurrent.Executor

internal var Glide.mainThreadExecutor: Executor
	get() = Executors.mainThreadExecutor()
	set(value) {
		logReplace(mainThreadExecutorField, value)
		mainThreadExecutorField.set(this, value)
	}

private val mainThreadExecutorField: Field by lazy {
	try {
		Executors::class.java
			.getDeclaredField("MAIN_THREAD_EXECUTOR")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide MAIN_THREAD_EXECUTOR cannot be found", ex)
	}
}
