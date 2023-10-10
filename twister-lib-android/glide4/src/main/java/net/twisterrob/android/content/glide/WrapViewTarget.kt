package net.twisterrob.android.content.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

/**
 * [ImageView] must be part of the hierarchy, it's parent will be used to measure the Glide load.
 * The size of it will match parent when it has no contents and wrap content when the resource is set in it.
 * It is suggested that the image view has [ImageView.setAdjustViewBounds] as `true`.
 *
 * @param target containing the view
 */
open class WrapViewTarget<Z : Any>(
	target: ImageViewTarget<Z>,
) : WrappingTarget<Z>(target) {

	private val imageView: ImageView = target.view
	private val subsitute: Target<Any> = SizeDeterminerHackTarget(imageView.parent as View)

	override fun getSize(cb: SizeReadyCallback) {
		// Using the parent for sizing because otherwise the placeholder would be measured.
		// For example: if that's a ColorDrawable it has no intrinsic size and the ImageView lays out as 1x1 
		subsitute.getSize(cb)
	}

	override fun removeCallback(cb: SizeReadyCallback) {
		subsitute.removeCallback(cb)
	}

	override fun onLoadFailed(errorDrawable: Drawable?) {
		update(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
		super.onLoadFailed(errorDrawable)
	}

	override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
		update(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
		super.onResourceReady(resource, transition)
	}

	override fun onLoadCleared(placeholder: Drawable?) {
		update(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
		super.onLoadCleared(placeholder)
	}

	override fun onLoadStarted(placeholder: Drawable?) {
		update(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
		super.onLoadStarted(placeholder)
	}

	protected fun update(width: Int, height: Int) {
		imageView.layout(0, 0, 0, 0)
		imageView.updateLayoutParams {
			this.width = width
			this.height = height
		}
	}
}
