package net.twisterrob.android.test.espresso;

import org.hamcrest.Matcher;

import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.core.internal.deps.guava.base.Throwables;

/**
 * A {@link FailureHandler} that just fails the test, no other "magic".
 * See {@link androidx.test.espresso.base.DefaultFailureHandler} for the default implementation.
 *
 * @noinspection JavadocReference
 * @see androidx.test.espresso.base.ThrowableHandler for en equivalent (but hidden) implementation.
 */
class JustFailFailureHandler implements FailureHandler {

	@Override public void handle(Throwable error, Matcher<View> viewMatcher) {
		Throwables.throwIfUnchecked(error);
		throw new RuntimeException(error);
	}
}
