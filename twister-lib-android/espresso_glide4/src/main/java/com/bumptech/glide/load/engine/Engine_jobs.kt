@file:JvmMultifileClass
@file:JvmName("EngineAccessor")

package com.bumptech.glide.load.engine

import java.lang.reflect.Field

internal val Engine.jobs: Jobs
	get() =
		try {
			jobsField[this] as Jobs
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Engine.jobs", ex)
		}

private val jobsField: Field by lazy {
	try {
		Engine::class.java
			.getDeclaredField("jobs")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide Engine jobs cannot be found", ex)
	}
}
