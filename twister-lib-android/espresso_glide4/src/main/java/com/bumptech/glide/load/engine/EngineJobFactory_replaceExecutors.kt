package com.bumptech.glide.load.engine

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.bumptech.glide.Glide
import com.bumptech.glide.engine
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.engine.executor.delegate
import net.twisterrob.android.test.espresso.idle.CountingExecutor
import net.twisterrob.android.test.espresso.idle.CountingExecutorService
import net.twisterrob.android.test.espresso.idle.GlideIdlingResourceRule
import net.twisterrob.android.test.espresso.idle.named
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.util.concurrent.Executor
import kotlin.reflect.KMutableProperty0

private val LOG = LoggerFactory.getLogger(GlideIdlingResourceRule::class.java)

internal fun Any?.logReplace(field: Field, newValue: Any) {
	LOG.trace("Replacing ${field}:\n${field.get(this)}\nto\n${newValue}")
}

internal fun replaceExecutors(glide: Glide): List<IdlingResource> =
	buildList {
		with(glide.engine.engineJobFactory) {
			add(idleExecutor(this::diskCacheExecutorHack, "Glide diskCacheExecutor"))
			add(idleExecutor(this::sourceExecutorHack, "Glide sourceExecutor"))
			add(idleExecutor(this::sourceUnlimitedExecutorHack, "Glide sourceUnlimitedExecutor"))
			add(idleExecutor(this::animationExecutorHack, "Glide animationExecutor"))
		}
		add(idleExecutor(::mainThreadExecutor, "Glide mainThreadExecutor"))
		add(idleExecutor(::directExecutor, "Glide directExecutor"))
	}

@JvmName("idleGlideExecutor")
private fun idleExecutor(prop: KMutableProperty0<GlideExecutor>, name: String): IdlingResource {
	val glideExecutor = prop.get()
	check(glideExecutor.delegate !is CountingExecutorService) { "Already wrapped ${name}: ${glideExecutor.delegate}" }
	val resource = CountingIdlingResource(name, true)
	prop.set(GlideExecutor(CountingExecutorService(resource, glideExecutor)))
	return resource.named()
}

private fun idleExecutor(prop: KMutableProperty0<Executor>, name: String): IdlingResource {
	val executor = prop.get()
	check(executor !is CountingExecutor) { "Already wrapped ${name}: ${executor}" }
	val resource = CountingIdlingResource(name, true)
	prop.set(CountingExecutor(resource, executor))
	return resource.named()
}
