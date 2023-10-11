package com.bumptech.glide.load.engine

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.bumptech.glide.Glide
import com.bumptech.glide.engine
import com.bumptech.glide.load.engine.Engine.EngineJobFactory
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.engine.executor.delegate
import net.twisterrob.android.test.espresso.idle.CompositeIdlingResource
import net.twisterrob.android.test.espresso.idle.CountingExecutorService
import net.twisterrob.android.test.espresso.idle.GlideIdlingResource
import net.twisterrob.android.test.espresso.idle.named
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty0

private val LOG = LoggerFactory.getLogger(GlideIdlingResource::class.java)

private var verboseReplace: Boolean = false

internal fun Any?.logReplace(field: Field, newValue: Any) {
	if (verboseReplace) {
		LOG.trace("Replacing ${field}:\n${field.get(this)}\nto\n${newValue}")
	}
}

internal fun idleExecutors(glide: Glide, verbose: Boolean): IdlingResource =
	CompositeIdlingResource(
		"Glide executors",
		*glide.engine.engineJobFactory.idleExecutors(verbose),
		verbose = verbose
	)

private fun EngineJobFactory.idleExecutors(verbose: Boolean): Array<IdlingResource> =
	arrayOf(
		this::diskCacheExecutorHack.replace("Glide diskCacheExecutor", verbose),
		this::sourceExecutorHack.replace("Glide sourceExecutor", verbose),
		this::sourceUnlimitedExecutorHack.replace("Glide sourceUnlimitedExecutor", verbose),
		this::animationExecutorHack.replace("Glide animationExecutor", verbose),
	)

@JvmName("idleGlideExecutor")
private fun KMutableProperty0<GlideExecutor>.replace(name: String, verbose: Boolean) : IdlingResource {
	val glideExecutor = this.get()
	check(glideExecutor.delegate !is CountingExecutorService) {
		"Already wrapped ${name}: ${glideExecutor.delegate}"
	}
	verboseReplace = verbose
	try {
		val resource = CountingIdlingResource(name, verbose)
		this.set(GlideExecutor(CountingExecutorService(resource, glideExecutor)))
		return resource.named()
	} finally {
		verboseReplace = false
	}
}
