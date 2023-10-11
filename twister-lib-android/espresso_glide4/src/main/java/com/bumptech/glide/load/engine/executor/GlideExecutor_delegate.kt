package com.bumptech.glide.load.engine.executor

import java.lang.reflect.Field
import java.util.concurrent.ExecutorService

internal val GlideExecutor.delegate: ExecutorService
	get() =
		try {
			delegateField.get(this) as ExecutorService
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack GlideExecutor.delegate", ex)
		}

private val delegateField: Field by lazy {
	try {
		GlideExecutor::class.java
			.getDeclaredField("delegate")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide Executor delegate cannot be found", ex)
	}
}
