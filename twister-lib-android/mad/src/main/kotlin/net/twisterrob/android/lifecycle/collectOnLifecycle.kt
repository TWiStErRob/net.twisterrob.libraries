package net.twisterrob.android.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.collectOnLifecycle(
	owner: LifecycleOwner, // TODO context parameter
	state: Lifecycle.State = Lifecycle.State.STARTED,
	collector: FlowCollector<T>
) {
	owner.lifecycleScope.launch {
		owner.repeatOnLifecycle(state) {
			this@collectOnLifecycle.collect(collector)
		}
	}
}
