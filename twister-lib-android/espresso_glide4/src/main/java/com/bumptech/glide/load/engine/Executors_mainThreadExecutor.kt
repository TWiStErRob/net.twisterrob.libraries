package com.bumptech.glide.load.engine

import com.bumptech.glide.util.Executors
import java.lang.reflect.Field
import java.util.concurrent.Executor

internal var mainThreadExecutor: Executor
	get() = Executors.mainThreadExecutor()
	set(value) {
		null.logReplace(mainThreadExecutorField, value)
		mainThreadExecutorField.set(null, value)
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
