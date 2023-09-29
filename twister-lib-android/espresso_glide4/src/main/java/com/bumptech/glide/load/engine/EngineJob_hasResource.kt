@file:JvmMultifileClass
@file:JvmName("EngineJobAccessor")

package com.bumptech.glide.load.engine

import net.twisterrob.java.utils.ReflectionTools

internal val EngineJob<*>.hasResource: Boolean
	@JvmName("hasResource")
	get() = ReflectionTools.get(this, "hasResource")
