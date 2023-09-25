package net.twisterrob.android.content.glide

import com.bumptech.glide.request.target.Target

open class WrappingTarget<R : Any>(
	protected val wrapped: Target<R>,
) : Target<R> by wrapped {

	override fun toString(): String =
		"Wrapped $wrapped"

	override fun equals(other: Any?): Boolean =
		if (other is WrappingTarget<*>) {
			wrapped == other.wrapped
		} else {
			super.equals(other)
		}

	override fun hashCode(): Int =
		wrapped.hashCode()
}
