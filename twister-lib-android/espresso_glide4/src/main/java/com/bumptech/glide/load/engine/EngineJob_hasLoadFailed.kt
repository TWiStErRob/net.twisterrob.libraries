@file:JvmMultifileClass
@file:JvmName("EngineJobAccessor")

package com.bumptech.glide.load.engine

import net.twisterrob.java.utils.ReflectionTools

internal val EngineJob<*>.hasLoadFailed: Boolean
	@JvmName("hasLoadFailed")
	get() = ReflectionTools.get(this, "hasLoadFailed")
