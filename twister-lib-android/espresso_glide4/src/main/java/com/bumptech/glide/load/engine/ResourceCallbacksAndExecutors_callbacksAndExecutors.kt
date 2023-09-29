@file:JvmMultifileClass
@file:JvmName("ResourceCallbacksAndExecutorsAccessor")

package com.bumptech.glide.load.engine

import com.bumptech.glide.load.engine.EngineJob.ResourceCallbackAndExecutor
import com.bumptech.glide.load.engine.EngineJob.ResourceCallbacksAndExecutors
import java.lang.reflect.Field

internal var ResourceCallbacksAndExecutors.callbacksAndExecutors: List<ResourceCallbackAndExecutor>
	get() =
		try {
			@Suppress("UNCHECKED_CAST")
			callbacksAndExecutorsField.get(this) as List<ResourceCallbackAndExecutor>
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack ResourceCallbacksAndExecutors.callbacksAndExecutors", ex)
		}
	set(value) =
		try {
			callbacksAndExecutorsField.set(this, value)
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack ResourceCallbacksAndExecutors.callbacksAndExecutors", ex)
		}

private val callbacksAndExecutorsField: Field by lazy {
	try {
		ResourceCallbacksAndExecutors::class.java.getDeclaredField("callbacksAndExecutors")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide callbacksAndExecutors cannot be found", ex)
	}
}
