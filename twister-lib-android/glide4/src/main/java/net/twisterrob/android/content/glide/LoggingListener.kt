package net.twisterrob.android.content.glide

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import net.twisterrob.android.content.glide.LoggingListener.ModelFormatter
import net.twisterrob.java.annotations.DebugHelper
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(LoggingListener::class.java)

@DebugHelper
class LoggingListener<R : Any> @JvmOverloads constructor(
	private val type: String,
	private val formatter: ModelFormatter<Any?> = ModelFormatter(Any?::toString),
) : RequestListener<R> {

	override fun onLoadFailed(
		e: GlideException?,
		model: Any?,
		target: Target<R>,
		isFirstResource: Boolean,
	): Boolean {
		LOG.warn(
			"Cannot load {}@{} into {} (first={})",
			type, formatter.toString(model), target, isFirstResource, e
		)
		return false
	}

	override fun onResourceReady(
		resource: R,
		model: Any,
		target: Target<R>,
		dataSource: DataSource,
		isFirstResource: Boolean,
	): Boolean {
		LOG.trace(
			"Loaded {}@{} into {} (first={}, source={}) transcoded={}",
			type, formatter.toString(model), target, isFirstResource, dataSource, toString(resource)
		)
		return false
	}

	private fun toString(resource: Any?): String =
		when {
			resource == null ->
				"null"

			resource is Bitmap -> {
				val width = resource.width
				val height = resource.height
				"Bitmap(${width}x${height})@${mem(resource)}"
			}

			resource is BitmapDrawable -> {
				val bitmap = toString(resource.bitmap)
				"${bitmap} in BitmapDrawable@${mem(resource)}"
			}

			VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && resource is VectorDrawable -> {
				val width = resource.intrinsicWidth
				val height = resource.intrinsicHeight
				"VectorDrawable(${width}x$height)@${mem(resource)}"
			}

			else ->
				resource.toString()
		}

	companion object {
		private fun mem(resource: Any?): String =
			Integer.toHexString(System.identityHashCode(resource))
	}

	fun interface ModelFormatter<in T> {
		fun toString(model: T): String

		companion object {
			@JvmStatic
			fun forResources(context: Context): ModelFormatter<Int> =
				ModelFormatter { model: Int ->
					try {
						context.resources.getResourceName(model)
							.replace(context.packageName, "app")
					} catch (ex: NotFoundException) {
						"${Integer.toHexString(model)}=${model}"
					}
				}
		}
	}
}
