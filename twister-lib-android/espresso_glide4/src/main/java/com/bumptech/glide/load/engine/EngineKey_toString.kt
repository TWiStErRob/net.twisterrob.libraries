@file:JvmName("EngineKeyAccessor")

package com.bumptech.glide.load.engine

import net.twisterrob.java.utils.ReflectionTools

internal fun EngineKey.toStringHack(): String =
	"${model}[${width}x${height}]"

private val EngineKey.model: Any?
	get() = ReflectionTools.get(this, "model")

private val EngineKey.height: Int
	get() = ReflectionTools.get(this, "height")

private val EngineKey.width: Int
	get() = ReflectionTools.get(this, "width")
