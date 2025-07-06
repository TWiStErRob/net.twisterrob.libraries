package net.twisterrob.android.view.layout;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.annotation.NonNull;

/**
 * @see <a href="http://stackoverflow.com/a/29172475/253468">StackOverflow</a>
 */
public abstract class DoAfterLayout implements OnGlobalLayoutListener {
	private final View view;
	protected DoAfterLayout(@NonNull View view) {
		this(view, false);
	}
	@SuppressWarnings("this-escape") // The object is readily constructed when onLayout is called.
	protected DoAfterLayout(@NonNull View view, boolean allowImmediate) {
		this.view = view;
		if (allowImmediate && !view.isLayoutRequested()
				&& (VERSION.SDK_INT < VERSION_CODES.KITKAT || view.isLaidOut())) {
			onLayout(view);
		} else {
			view.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}

	@SuppressWarnings("deprecation")
	@Override public final void onGlobalLayout() {
		// requests a non-floating observer which is guaranteed to be alive
		// this prevents IllegalStateException: "This ViewTreeObserver is not alive, call getViewTreeObserver() again"
		ViewTreeObserver observer = view.getViewTreeObserver();
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			observer.removeOnGlobalLayoutListener(this);
		} else {
			observer.removeGlobalOnLayoutListener(this);
		}
		onLayout(view);
	}
	protected abstract void onLayout(@NonNull View view);
}
