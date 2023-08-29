package net.twisterrob.android.activity

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import net.twisterrob.android.capture_image.R
import net.twisterrob.android.utils.tools.IntentTools
import net.twisterrob.java.io.IOTools
import java.io.IOException

class PickDialogActor {

	fun cancel() {
		pressBack()
	}

	fun captureFromCamera(): PopupIntent<Uri> =
		object : PopupIntent<Uri> {
			override fun open() {
				onView(withText(R.string.image__choose_external__capture_title)).perform(click())
			}

			override fun intend(output: Uri) {
				intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
					.respondWithFunction { intent: Intent ->
						copyToUriExtra(output, intent, MediaStore.EXTRA_OUTPUT)
						ActivityResult(Activity.RESULT_OK, null)
					}
			}

			override fun intendCancelled() {
				intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
					.respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
			}

			override fun verify(count: Int) {
				intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE), times(count))
			}
		}

	fun pick(): PopupIntent<Uri> =
		object : PopupIntent<Uri> {
			override fun open() {
				onView(withText(R.string.image__choose_external__pick_title)).perform(click())
			}

			override fun intend(output: Uri) {
				intending(hasAction(Intent.ACTION_PICK))
					.respondWith(ActivityResult(Activity.RESULT_OK, Intent().setData(output)))
			}

			override fun intendCancelled() {
				intending(hasAction(Intent.ACTION_PICK))
					.respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
			}

			override fun verify(count: Int) {
				intended(hasAction(Intent.ACTION_PICK), times(count))
			}
		}

	interface PopupIntent<O> {
		fun open()
		fun intend(output: O)
		fun intendCancelled()
		fun verify(count: Int)
	}
}

private fun copyToUriExtra(data: Uri, intent: Intent, extraKey: String) {
	val output = IntentTools.getParcelableExtra(intent, extraKey, Uri::class.java)
		?: error("Intent ${intent} does not contain ${extraKey}")
	val context = InstrumentationRegistry.getInstrumentation().context
	try {
		IOTools.copyStream(
			context.contentResolver.openInputStream(data),
			context.contentResolver.openOutputStream(output)
		)
	} catch (ex: IOException) {
		throw IllegalStateException("Cannot copy ${data} to ${output}", ex)
	}
}
