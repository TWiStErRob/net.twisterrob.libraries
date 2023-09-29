@file:JvmMultifileClass
@file:JvmName("EngineJobAccessor")

package com.bumptech.glide.load.engine

import com.bumptech.glide.load.engine.EngineJob.ResourceCallbackAndExecutor

internal var EngineJob<*>.callbacks: List<ResourceCallbackAndExecutor>
	get() = this.cbs.callbacksAndExecutors
	set(value) {
		this.cbs.callbacksAndExecutors = value
	}
