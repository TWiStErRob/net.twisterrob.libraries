package net.twisterrob.android.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.android.mad.test.R
import net.twisterrob.android.mad.test.databinding.TestContentBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

class ViewBindingsTest {

	@Test
	fun testViewBindingInflate() {
		launchActivity<ViewBindingInflateActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingInflateActivity : ComponentActivity() {
		private val binding: TestContentBinding by viewBindingInflate()
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingInflateAutoDestroy() {
		launchActivity<ViewBindingInflateAutoDestroyActivity>().use { scenario ->
			assertViewVisible()
			scenario.onActivity { activity ->
				assertTrue(activity::binding.lazyDelegate.isInitialized())
			}
			// Capture so that we can inspect the instance after destroy, otherwise:
			// > Cannot run onActivity since Activity has been destroyed already
			lateinit var destroyedActivity: ViewBindingInflateAutoDestroyActivity
			scenario.onActivity { destroyedActivity = it }
			scenario.moveToState(Lifecycle.State.DESTROYED)
			Espresso.onIdle() // Wait so that the main thread has time to process.
			assertFalse(destroyedActivity::binding.lazyDelegate.isInitialized())
		}
	}

	class ViewBindingInflateAutoDestroyActivity : ComponentActivity() {
		@VisibleForTesting
		val binding: TestContentBinding by viewBindingInflate()
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingInflateNoAutoDestroy() {
		launchActivity<ViewBindingInflateNoAutoDestroyActivity>().use { scenario ->
			assertViewVisible()
			scenario.onActivity { activity ->
				assertTrue(activity::binding.lazyDelegate.isInitialized())
			}
			// Capture so that we can inspect the instance after destroy, otherwise:
			// > Cannot run onActivity since Activity has been destroyed already
			lateinit var destroyedActivity: ViewBindingInflateNoAutoDestroyActivity
			scenario.onActivity { destroyedActivity = it }
			scenario.moveToState(Lifecycle.State.DESTROYED)
			Espresso.onIdle() // Wait so that the main thread has time to process.
			assertTrue(destroyedActivity::binding.lazyDelegate.isInitialized())
		}
	}

	class ViewBindingInflateNoAutoDestroyActivity : ComponentActivity() {
		@VisibleForTesting
		val binding: TestContentBinding by viewBindingInflate(autoDestroy = false)
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingInflateDirect() {
		launchActivity<ViewBindingInflateDirectActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingInflateDirectActivity : ComponentActivity() {
		private val binding by viewBindingInflate(TestContentBinding::inflate)
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingInflateNoSet() {
		launchActivity<ViewBindingInflateNoSetActivity>().use {
			assertNoView()
		}
	}

	class ViewBindingInflateNoSetActivity : ComponentActivity() {
		private val binding: TestContentBinding by viewBindingInflate(setContentView = false)
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingInflateNoSetUsed() {
		launchActivity<ViewBindingInflateNoSetUsedActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingInflateNoSetUsedActivity : ComponentActivity() {
		private val binding: TestContentBinding by viewBindingInflate(setContentView = false)
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			setContentView(binding.root)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingConstructor() {
		launchActivity<ViewBindingConstructorActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingConstructorActivity : ComponentActivity(R.layout.test_content) {
		private val binding: TestContentBinding by viewBinding()
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingConstructorDirect() {
		launchActivity<ViewBindingConstructorDirectActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingConstructorDirectActivity : ComponentActivity(R.layout.test_content) {
		private val binding by viewBinding(TestContentBinding::bind)
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingManualInflate() {
		launchActivity<ViewBindingManualActivity>().use {
			assertViewVisible()
		}
	}

	class ViewBindingManualActivity : ComponentActivity() {
		private val binding: TestContentBinding by viewBinding()
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			setContentView(R.layout.test_content)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingFragmentConstructor() {
		launchFragmentInContainer<ViewBindingConstructorFragment>().use {
			assertViewVisible()
		}
	}

	class ViewBindingConstructorFragment : Fragment(R.layout.test_content) {
		private val binding: TestContentBinding by viewBinding()

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingFragmentManual() {
		launchFragmentInContainer<ViewBindingManualFragment>().use {
			assertViewVisible()
		}
	}

	class ViewBindingManualFragment : Fragment() {
		private val binding: TestContentBinding by viewBinding()

		override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
		): View = inflater.inflate(R.layout.test_content, container, false)

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingFragmentAutoDestroy() {
		launchFragmentInContainer<ViewBindingAutoDestroyFragment>().use { scenario ->
			scenario.hasView(ViewBindingAutoDestroyFragment::binding)
			scenario.moveToState(Lifecycle.State.CREATED)
			scenario.noView(ViewBindingAutoDestroyFragment::binding)
			scenario.moveToState(Lifecycle.State.RESUMED)
			scenario.hasView(ViewBindingAutoDestroyFragment::binding)
		}
	}

	class ViewBindingAutoDestroyFragment : Fragment(R.layout.test_content) {
		@VisibleForTesting
		val binding: TestContentBinding by viewBinding()

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}

	@Test
	fun testViewBindingFragmentNoAutoDestroy() {
		launchFragmentInContainer<ViewBindingNoAutoDestroyFragment>().use { scenario ->
			scenario.hasView(ViewBindingNoAutoDestroyFragment::binding)
			scenario.moveToState(Lifecycle.State.CREATED)
			scenario.noView(ViewBindingNoAutoDestroyFragment::binding, expectedBinding = true)
			scenario.moveToState(Lifecycle.State.RESUMED)
			scenario.hasView(ViewBindingNoAutoDestroyFragment::binding)
		}
	}

	class ViewBindingNoAutoDestroyFragment : Fragment(R.layout.test_content) {
		@VisibleForTesting
		val binding: TestContentBinding by viewBinding(autoDestroy = false)

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
			binding.view.setOnClickListener { }
		}
	}
}

/**
 * @see R.layout.test_content
 * @see R.id.view
 * @see TestContentBinding.view
 */
private fun assertViewVisible() {
	onView(withId(R.id.view)).check(matches(isDisplayed()))
}

/**
 * @see R.layout.test_content
 * @see R.id.view
 * @see TestContentBinding.view
 */
private fun assertNoView() {
	onView(withId(R.id.view)).check(doesNotExist())
}

private fun <T : Fragment> FragmentScenario<T>.hasView(
	bindingProp: KProperty1<T, TestContentBinding>
) {
	assertViewVisible()
	onFragment { fragment ->
		assertNotNull(fragment.view)
		assertTrue(bindingProp.lazyDelegate(fragment).isInitialized())
	}
}

private fun <T : Fragment> FragmentScenario<T>.noView(
	bindingProp: KProperty1<T, TestContentBinding>,
	expectedBinding: Boolean = false,
) {
	assertNoView()
	onFragment { fragment ->
		assertNull(fragment.view)
		assertEquals(expectedBinding, bindingProp.lazyDelegate(fragment).isInitialized())
	}
}

/**
 * This accesses the delegate so that we can check if there's a value
 * without actually reading it (which would have a side effect of initializing it).
 */
private val KProperty0<*>.lazyDelegate: Lazy<*>
	get() {
		this.isAccessible = true
		return this.getDelegate() as Lazy<*>
	}

/**
 * This accesses the delegate so that we can check if there's a value
 * without actually reading it (which would have a side effect of initializing it).
 */
private fun <T> KProperty1<T, *>.lazyDelegate(receiver: T): Lazy<*> {
	this.isAccessible = true
	return this.getDelegate(receiver) as Lazy<*>
}
