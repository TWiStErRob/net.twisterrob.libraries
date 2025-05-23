package net.twisterrob.android.test.espresso.idle;

import java.util.List;

import org.hamcrest.Matcher;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;

import androidx.test.espresso.Root;

import net.twisterrob.android.test.espresso.DialogMatchers;
import net.twisterrob.android.test.espresso.EspressoExtensions;

public class ToastIdlingResource extends AsyncIdlingResource {
	@Override public String getName() {
		return "Toast";
	}

	@Override protected boolean isIdle() {
		return getToast() == null;
	}

	@Override protected void waitForIdleAsync() {
		Root toast = getToast();
		if (toast != null && VERSION_CODES.HONEYCOMB_MR1 <= VERSION.SDK_INT) {
			toast.getDecorView().addOnAttachStateChangeListener(transitionOnDetach);
		} else {
			// let Espresso do its usual exponential backoff thing
		}
	}

	private Root getToast() {
		Matcher<Root> toast = DialogMatchers.isToast();
		List<Root> roots = EspressoExtensions.getRoots();
		for (Root root : roots) {
			if (toast.matches(root)) {
				return root;
			}
		}
		return null;
	}

	private final OnAttachStateChangeListener transitionOnDetach = VERSION.SDK_INT < VERSION_CODES.HONEYCOMB_MR1
			? null : new OnAttachStateChangeListener() {
		@Override public void onViewAttachedToWindow(View v) {
			v.removeOnAttachStateChangeListener(this);
			throw new IllegalStateException("Toast shouldn't be re-attached.");
		}
		@Override public void onViewDetachedFromWindow(View v) {
			v.removeOnAttachStateChangeListener(this);
			transitionToIdle();
		}
	};
}
