package net.twisterrob.android.content.glide

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.Transition.ViewAdapter

open class MenuItemTarget(
	private val menuItem: MenuItem,
) : Target<Drawable>, ViewAdapter {

	private var request: Request? = null

	override fun getRequest(): Request? =
		request

	override fun setRequest(request: Request?) {
		this.request = request
	}

	override fun getView(): View =
		error("MenuItem has no view, but supports animations via Drawables.")

	override fun getCurrentDrawable(): Drawable? =
		menuItem.icon

	override fun setDrawable(drawable: Drawable?) {
		menuItem.icon = drawable
	}

	override fun onLoadStarted(placeholder: Drawable?) {
		setDrawable(placeholder)
	}

	override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
		if (transition == null || !transition.transition(resource, this)) {
			setDrawable(resource)
		}
	}

	override fun onLoadFailed(errorDrawable: Drawable?) {
		setDrawable(errorDrawable)
	}

	override fun onLoadCleared(placeholder: Drawable?) {
		setDrawable(placeholder)
	}

	override fun getSize(cb: SizeReadyCallback) {
		val px = 24.dpAsPx.toInt()
		cb.onSizeReady(px, px)
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

private val Int.dpAsPx: Float
	get() =
		TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			this.toFloat(),
			Resources.getSystem().displayMetrics
		)
