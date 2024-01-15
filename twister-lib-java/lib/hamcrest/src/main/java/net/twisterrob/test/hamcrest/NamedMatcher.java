package net.twisterrob.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class NamedMatcher<T> extends WrappingMatcher<T> {
	private final String name;

	public NamedMatcher(String name, Matcher<T> matcher) {
		super(matcher);
		this.name = name;
	}

	@Override public void describeTo(Description description) {
		description.appendText(name);
	}
}
