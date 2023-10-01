@file:JvmMultifileClass
@file:JvmName("JobsAccessor")

package com.bumptech.glide.load.engine

import com.bumptech.glide.load.Key
import java.lang.reflect.Field

internal var Jobs.onlyCacheJobs: Map<Key, EngineJob<*>>
	get() =
		try {
			@Suppress("UNCHECKED_CAST")
			onlyCacheJobsField.get(this) as Map<Key, EngineJob<*>>
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Jobs.onlyCacheJobs", ex)
		}
	set(value) {
		try {
			onlyCacheJobsField.set(this, value)
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Jobs.onlyCacheJobs", ex)
		}
	}

private val onlyCacheJobsField: Field by lazy {
	try {
		Jobs::class.java
			.getDeclaredField("onlyCacheJobs")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide Jobs.onlyCacheJobs cannot be found", ex)
	}
}
