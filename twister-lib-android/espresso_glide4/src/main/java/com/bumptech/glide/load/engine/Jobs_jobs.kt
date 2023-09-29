@file:JvmMultifileClass
@file:JvmName("JobsAccessor")

package com.bumptech.glide.load.engine

import com.bumptech.glide.load.Key
import java.lang.reflect.Field

internal var Jobs.jobs: Map<Key, EngineJob<*>>
	get() =
		try {
			@Suppress("UNCHECKED_CAST")
			jobsField.get(this) as Map<Key, EngineJob<*>>
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Jobs.jobs", ex)
		}
	set(value) {
		try {
			jobsField.set(this, value)
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Jobs.jobs", ex)
		}
	}

private val jobsField: Field by lazy {
	try {
		Jobs::class.java
			.getDeclaredField("jobs")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide Jobs.jobs cannot be found", ex)
	}
}
