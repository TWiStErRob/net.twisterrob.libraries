package com.bumptech.glide

import com.bumptech.glide.load.engine.Engine
import java.lang.reflect.Field

internal val Glide.engine: Engine
	get() =
		try {
			engineField.get(this) as Engine
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Glide.engine", ex)
		}

private val engineField: Field by lazy {
	try {
		Glide::class.java
			.getDeclaredField("engine")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide engine cannot be found", ex)
	}
}
