package net.twisterrob.android.content.glide.svg

import android.view.View
import androidx.annotation.IntDef
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import net.twisterrob.android.content.glide.svg.LayerTypeSetterListener.TargetViewGetter
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Listener which can be used for example to
 * update an [ImageView][android.widget.ImageView] to be software rendered,
 * because [SVG][com.caverock.androidsvg.SVG]/[Picture][android.graphics.Picture]
 * can't render on a hardware backed [Canvas][android.graphics.Canvas].
 *
 * @param R not used, exists to prevent unchecked warnings at usage
 */
class LayerTypeSetterListener<R : Any> @JvmOverloads constructor(
	@LayerType
	private val layerType: Int,
	private val getter: TargetViewGetter = TargetViewGetter.VIEW_TARGET,
) : RequestListener<R> {

	override fun onResourceReady(
		resource: R, model: Any, target: Target<R>, dataSource: DataSource, isFirstResource: Boolean
	): Boolean = setLayerType(target)

	override fun onLoadFailed(
		e: GlideException?, model: Any?, target: Target<R>, isFirstResource: Boolean
	): Boolean = setLayerType(target)

	private fun setLayerType(target: Target<R>): Boolean {
		val view = getter.getView(target)
		view.setLayerType(layerType, null)
		return false
	}

	fun interface TargetViewGetter {
		fun getView(target: Target<*>): View

		companion object {

			@JvmStatic
			val VIEW_TARGET: TargetViewGetter = TargetViewGetter { target ->
				// TODEL https://github.com/bumptech/glide/issues/3332 for now RequestBuilder.into returns this.
				@Suppress("DEPRECATION")
				(target as com.bumptech.glide.request.target.ViewTarget<*, *>).view
			}

			@JvmStatic
			val CUSTOM_VIEW_TARGET: TargetViewGetter = TargetViewGetter { target ->
				(target as CustomViewTarget<*, *>).view
			}
		}
	}

	@IntDef(
		View.LAYER_TYPE_NONE,
		View.LAYER_TYPE_SOFTWARE,
		View.LAYER_TYPE_HARDWARE,
	)
	@Retention(SOURCE)
	private annotation class LayerType
}
