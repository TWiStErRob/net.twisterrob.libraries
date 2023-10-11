package com.bumptech.glide.load.engine

import java.lang.reflect.Field

internal val Engine.engineJobFactory: Engine.EngineJobFactory
	get() =
		try {
			engineJobFactoryField.get(this) as Engine.EngineJobFactory
		} catch (ex: IllegalAccessException) {
			throw IllegalStateException("Cannot hack Engine.engineJobFactory", ex)
		}

private val engineJobFactoryField: Field by lazy {
	try {
		Engine::class.java
			.getDeclaredField("engineJobFactory")
			.apply { isAccessible = true }
	} catch (ex: Exception) {
		throw IllegalStateException("Glide Engine engineJobFactory cannot be found", ex)
	}
}
