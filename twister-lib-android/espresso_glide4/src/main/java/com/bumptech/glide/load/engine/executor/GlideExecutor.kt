package com.bumptech.glide.load.engine.executor

import android.annotation.SuppressLint
import java.util.concurrent.ExecutorService

@SuppressLint("VisibleForTests")
internal fun GlideExecutor(service: ExecutorService): GlideExecutor =
	GlideExecutor::class.java
		.getDeclaredConstructor(ExecutorService::class.java)
		.newInstance(service)
