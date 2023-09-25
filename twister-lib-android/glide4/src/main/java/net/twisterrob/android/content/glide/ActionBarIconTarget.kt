package net.twisterrob.android.content.glide

import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import net.twisterrob.android.utils.tools.ResourceTools

class ActionBarIconTarget(
	private val actionBar: ActionBar,
) : Target<Drawable> {

	private var request: Request? = null

	override fun getRequest(): Request? =
		request

	override fun setRequest(request: Request?) {
		this.request = request
	}

	override fun onLoadStarted(placeholder: Drawable?) {
		actionBar.setIcon(placeholder)
	}

	override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
		// Transitions are not supported because it's not "possible" to get the icon/logo view from an Action Bar/ToolBar.
		actionBar.setIcon(resource)
	}

	override fun onLoadFailed(errorDrawable: Drawable?) {
		actionBar.setIcon(errorDrawable)
	}

	override fun onLoadCleared(placeholder: Drawable?) {
		actionBar.setIcon(placeholder)
	}

	override fun getSize(cb: SizeReadyCallback) {
		val attr = android.R.attr.actionBarSize
		val tv = TypedValue()
		val size = if (actionBar.themedContext.theme.resolveAttribute(attr, tv, true)) {
			val metrics = actionBar.themedContext.resources.displayMetrics
			TypedValue.complexToDimensionPixelSize(tv.data, metrics)
		} else {
			ResourceTools.dipInt(actionBar.themedContext, 48f) // standard size
		}
		cb.onSizeReady(size, size)
	}

	override fun removeCallback(cb: SizeReadyCallback) {
		// No op, because we don't hold a reference to cb in getSize.
	}

	override fun onStart() {
		// No op, because there's nothing to do when lifecycle changes.
	}

	override fun onStop() {
		// No op, because there's nothing to do when lifecycle changes.
	}

	override fun onDestroy() {
		// No op, because there's nothing to do when lifecycle changes.
	}
}
