package net.twisterrob.android.content.glide

import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory

//.transition(GenericTransitionOptions.with(new ColorFilterApplyingTransitionFactory(filter)))
//.transition(ColorFilterApplyingTransitionFactory.with(filter))
class ColorFilterApplyingTransitionFactory(
	private val filter: ColorFilter
) :	TransitionFactory<Drawable> {

	override fun build(dataSource: DataSource, isFirstResource: Boolean): Transition<Drawable> =
		Transition { current, _ ->
			if (current is PictureDrawable) {
				throw UnsupportedOperationException("PictureDrawable does not support color filters.")
			}
			current.colorFilter = filter
			false // Synchronously set the drawable in the target.
		}

	companion object {
		@JvmStatic
		fun with(filter: ColorFilter): GenericTransitionOptions<Drawable> =
			GenericTransitionOptions.with(ColorFilterApplyingTransitionFactory(filter))
	}
}
