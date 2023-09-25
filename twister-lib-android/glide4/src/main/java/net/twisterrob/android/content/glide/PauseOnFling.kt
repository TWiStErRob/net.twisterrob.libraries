package net.twisterrob.android.content.glide

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.RequestManager
import kotlin.math.absoluteValue

class PauseOnFling(
	private val glide: RequestManager,
) : OnScrollListener() {

	private var dragging = false

	override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
		dragging = when (newState) {
			RecyclerView.SCROLL_STATE_DRAGGING -> true
			RecyclerView.SCROLL_STATE_IDLE -> false
			RecyclerView.SCROLL_STATE_SETTLING -> false
			else -> error("Unknown scroll state: ${newState}")
		}
		if (glide.isPaused) {
			when (newState) {
				RecyclerView.SCROLL_STATE_DRAGGING ->
					// User is touching the screen (can't be moving fast), show images.
					glide.resumeRequests()

				RecyclerView.SCROLL_STATE_IDLE ->
					// Scroll finished, show images.
					glide.resumeRequests()

				RecyclerView.SCROLL_STATE_SETTLING ->
					// Settling means the user let the screen go, but it can still be scrolling fast.
					Unit

				else ->
					error("Unknown scroll state: ${newState}")
			}
		}
	}

	override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
		if (!dragging) {
			// TODO can be made better by a rolling average of last N calls to smooth out patterns like a,b,a
			val currentSpeed = dy.absoluteValue
			val paused = glide.isPaused
			when {
				paused && currentSpeed < FLING_JUMP_LOW_THRESHOLD -> glide.resumeRequests()
				!paused && FLING_JUMP_HIGH_THRESHOLD < currentSpeed -> glide.pauseRequests()
				else -> Unit // Fling is still happening or it's already almost stopped, leave it as is.
			}
		}
	}

	companion object {
		private const val FLING_JUMP_LOW_THRESHOLD: Int = 80
		private const val FLING_JUMP_HIGH_THRESHOLD: Int = 120
	}
}
