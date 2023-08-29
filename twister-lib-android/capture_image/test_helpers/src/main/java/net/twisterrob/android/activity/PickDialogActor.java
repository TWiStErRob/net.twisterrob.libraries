package net.twisterrob.android.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import net.twisterrob.android.capture_image.R;

public class PickDialogActor {

	public void cancel() {
		Espresso.pressBack();
	}

	public @NonNull PopupIntent<Uri> pick() {
		return new PopupIntent<Uri>() {
			@Override public void open() {
				onView(withText(R.string.image__choose_external__pick_title)).perform(click());
			}
			@Override public void intend(Uri response) {
				Intents.intending(hasAction(Intent.ACTION_PICK))
				       .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent().setData(response)));
			}
			@Override public void intendCancelled() {
				Intents.intending(hasAction(Intent.ACTION_PICK))
				       .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));
			}
			@Override public void verify(int count) {
				Intents.intended(hasAction(Intent.ACTION_PICK), times(count));
			}
		};
	}

	public interface PopupIntent<O> {
		void open();
		void intend(O output);
		void intendCancelled();
		void verify(int count);
	}
}
