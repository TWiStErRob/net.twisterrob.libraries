package com.bumptech.glide.load.engine

import com.bumptech.glide.util.Executors
import java.lang.reflect.Field
import java.util.concurrent.Executor

internal var directExecutor: Executor
	get() = Executors.directExecutor()
	set(value) {
		null.logReplace(directExecutorField, value)
		directExecutorField.set(null, value)
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
