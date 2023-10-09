package com.bumptech.glide.load.engine

import com.bumptech.glide.Glide
import com.bumptech.glide.util.Executors
import java.lang.reflect.Field
import java.util.concurrent.Executor

internal var Glide.directExecutor: Executor
	get() = Executors.directExecutor()
	set(value) {
		logReplace(directExecutorField, value)
		directExecutorField.set(this, value)
	}

private val directExecutorField: Field by lazy {
	try {
		Executors::class.java
			.getDeclaredField("DIRECT_EXECUTOR")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide DIRECT_EXECUTOR cannot be found", ex)
	}
}
