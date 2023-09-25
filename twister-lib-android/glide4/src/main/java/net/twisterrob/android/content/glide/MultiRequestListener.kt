package net.twisterrob.android.content.glide

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MultiRequestListener<R : Any>(
	private val listeners: Collection<RequestListener<R>>,
) : RequestListener<R> {

	constructor(vararg listeners: RequestListener<R>) : this(listeners.toList())

	override fun onResourceReady(
		resource: R,
		model: Any,
		target: Target<R>,
		dataSource: DataSource,
		isFirstResource: Boolean,
	): Boolean =
		listeners.any { it.onResourceReady(resource, model, target, dataSource, isFirstResource) }

	override fun onLoadFailed(
		e: GlideException?,
		model: Any?,
		target: Target<R>,
		isFirstResource: Boolean,
	): Boolean =
		listeners.any { it.onLoadFailed(e, model, target, isFirstResource) }
}
