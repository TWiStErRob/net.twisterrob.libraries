package com.bumptech.glide

import com.bumptech.glide.load.engine.Engine

internal val Glide.engine: Engine
	get() = Glide::class.java
		.getDeclaredField("engine")
		.apply { isAccessible = true }
		.get(this) as Engine
