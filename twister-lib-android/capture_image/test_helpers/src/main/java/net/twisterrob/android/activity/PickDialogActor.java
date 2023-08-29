package net.twisterrob.android.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.utils.tools.IntentTools;
import net.twisterrob.java.io.IOTools;

public class PickDialogActor {

	public void cancel() {
		Espresso.pressBack();
	}

	public @NonNull PopupIntent<Uri> captureFromCamera() {
		return new PopupIntent<Uri>() {
			@Override public void open() {
				onView(withText(R.string.image__choose_external__capture_title)).perform(click());
			}
			@Override public void intend(Uri data) {
				Intents
						.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
						.respondWithFunction((intent) -> {
							copyToUriExtra(data, intent, MediaStore.EXTRA_OUTPUT);
							return new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
						});
			}
			@Override public void intendCancelled() {
				Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
				       .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));
			}
			@Override public void verify(int count) {
				Intents.intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE), times(count));
			}
		};
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

	private static void copyToUriExtra(@NonNull Uri data, @NonNull Intent intent, @NonNull String extraKey) {
		Uri output = IntentTools.getParcelableExtra(intent, extraKey, Uri.class);
		Context context = InstrumentationRegistry.getInstrumentation().getContext();
		try {
			IOTools.copyStream(
					context.getContentResolver().openInputStream(data),
					context.getContentResolver().openOutputStream(output)
			);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public interface PopupIntent<O> {
		void open();
		void intend(O output);
		void intendCancelled();
		void verify(int count);
	}
}
