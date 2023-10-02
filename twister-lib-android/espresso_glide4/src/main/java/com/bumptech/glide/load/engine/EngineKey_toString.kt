@file:JvmName("EngineKeyAccessor")

package com.bumptech.glide.load.engine

import net.twisterrob.java.utils.ReflectionTools

internal fun EngineKey.toStringHack(): String =
	"${model}[${width}x${height}]"

internal val EngineKey.model: Any?
	get() = ReflectionTools.get(this, "model")

internal val EngineKey.height: Int
	get() = ReflectionTools.get(this, "height")

internal val EngineKey.width: Int
	get() = ReflectionTools.get(this, "width")
