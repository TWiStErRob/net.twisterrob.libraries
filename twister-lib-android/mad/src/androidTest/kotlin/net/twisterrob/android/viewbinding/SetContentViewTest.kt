package net.twisterrob.android.viewbinding

import android.app.Activity
import android.os.Bundle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.twisterrob.android.mad.test.R
import net.twisterrob.android.mad.test.databinding.TestContentBinding
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetContentViewTest {

	@Test
	fun testInflateReference() {
		launchActivity<SetContentViewActivity>().use {
			onView(withId(R.id.view)).check(matches(isDisplayed()))
		}
	}

	class SetContentViewActivity : Activity() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			setContentView(TestContentBinding::inflate)
		}
	}
}
