package net.twisterrob.android.view

import android.os.Build.VERSION_CODES
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

interface ViewProvider {
	val view: View?

	class StaticViewProvider(
		override val view: View?,
	) : ViewProvider

	class SupportFragmentViewProvider(
		private val fragment: Fragment,
	) : ViewProvider {
		override val view: View?
			get() = fragment.view
	}

	@RequiresApi(VERSION_CODES.HONEYCOMB)
	class FragmentViewProvider(
		@Suppress("DEPRECATION")
		private val fragment: android.app.Fragment,
	) : ViewProvider {
		override val view: View?
			@Suppress("DEPRECATION")
			get() = fragment.view
	}
}
