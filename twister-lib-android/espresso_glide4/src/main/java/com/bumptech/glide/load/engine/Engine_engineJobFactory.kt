package com.bumptech.glide.load.engine

internal val Engine.engineJobFactory: Engine.EngineJobFactory
	get() = Engine::class.java
		.getDeclaredField("engineJobFactory")
		.apply { isAccessible = true }
		.get(this) as Engine.EngineJobFactory
