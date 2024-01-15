package net.twisterrob.android.test.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class OnceMatcher<T> extends BaseMatcher<T> {
	private final Matcher<T> matcher;
	private boolean matched = false;

	public OnceMatcher(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	@Override public boolean matches(Object item) {
		if (matched) {
			return false;
		}
		matched = matcher.matches(item);
		return matched;
	}

	@Override public void describeTo(Description description) {
		description.appendText("only once: ").appendDescriptionOf(matcher);
	}
}
