package net.twisterrob.android.content.glide

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

/**
 * [com.bumptech.glide.request.target.CustomViewTarget.SizeDeterminer] is not exposed,
 * so we have to use the public interface to get its behavior.
 *
 * No implementations (error) of any methods, because they shouldn't be ever called.
 * Use only [getSize] and [removeCallback].
 */
internal class SizeDeterminerHackTarget<T : View, Z : Any>(view: T) : CustomViewTarget<T, Z>(view) {
	override fun onResourceLoading(placeholder: Drawable?) {
		throw UnsupportedOperationException()
	}

	override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
		throw UnsupportedOperationException()
	}

	override fun onLoadFailed(errorDrawable: Drawable?) {
		throw UnsupportedOperationException()
	}

	override fun onResourceCleared(placeholder: Drawable?) {
		throw UnsupportedOperationException()
	}
}
