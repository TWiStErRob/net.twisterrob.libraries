package net.twisterrob.android.content.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.Transition.ViewAdapter
import java.util.IdentityHashMap

open class TextViewDrawableTarget<R : Drawable>(
	private val view: TextView,
	private val location: Location,
	private val width: Int,
	private val height: Int,
) : Target<R>, ViewAdapter {

	private var request: Request? = null
	private val internalCallbacks: MutableMap<SizeReadyCallback, Callback> = IdentityHashMap()

	override fun getView(): View =
		view

	override fun getRequest(): Request? =
		request

	override fun setRequest(request: Request?) {
		this.request = request
	}

	override fun getCurrentDrawable(): Drawable? =
		view.compoundDrawables[location.ordinal]

	override fun onResourceReady(resource: R, transition: Transition<in R>?) {
		if (transition == null || !transition.transition(resource, this)) {
			set(resource)
		}
	}

	override fun setDrawable(drawable: Drawable) {
		set(drawable)
	}

	override fun onLoadCleared(placeholder: Drawable?) {
		set(placeholder)
	}

	override fun onLoadStarted(placeholder: Drawable?) {
		set(placeholder)
	}

	override fun onLoadFailed(errorDrawable: Drawable?) {
		set(errorDrawable)
	}

	private fun set(drawable: Drawable?) {
		drawable?.setBounds(0, 0, width, height)
		view.setCompoundDrawables(
			selectForLocation(drawable, Location.LEFT),
			selectForLocation(drawable, Location.TOP),
			selectForLocation(drawable, Location.RIGHT),
			selectForLocation(drawable, Location.BOTTOM)
		)
	}

	private fun selectForLocation(drawable: Drawable?, location: Location): Drawable? =
		if (location == this.location) drawable else null

	override fun getSize(cb: SizeReadyCallback) {
		if (0 <= width && 0 <= height) {
			cb.onSizeReady(width, height)
		} else {
			val internalCb = SizeReadyCallback { width: Int, height: Int ->
				when (location) {
					Location.LEFT,
					Location.RIGHT -> cb.onSizeReady(Target.SIZE_ORIGINAL, height)
					Location.TOP,
					Location.BOTTOM -> cb.onSizeReady(width, Target.SIZE_ORIGINAL)
				}
			}
			val target = SizeDeterminerHackTarget<TextView, R>(view)
			internalCallbacks[cb] = Callback(internalCb, target)
			target.getSize(internalCb)
		}
	}

	override fun removeCallback(cb: SizeReadyCallback) {
		val callback = internalCallbacks.remove(cb)
		callback?.target?.removeCallback(callback.internal)
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

	enum class Location {
		LEFT,
		TOP,
		RIGHT,
		BOTTOM,
	}

	private class Callback(
		val internal: SizeReadyCallback,
		val target: Target<*>,
	)
}
