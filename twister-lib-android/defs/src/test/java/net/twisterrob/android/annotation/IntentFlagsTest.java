package net.twisterrob.android.annotation;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import static android.content.Intent.FLAG_FROM_BACKGROUND;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class IntentFlagsTest {
	@Test public void test() {
		String string = IntentFlags.Converter.toString(FLAG_GRANT_READ_URI_PERMISSION | FLAG_FROM_BACKGROUND, null);
		assertThat(string, containsString("[FLAG_GRANT_READ_URI_PERMISSION* | FLAG_FROM_BACKGROUND]"));
	}
}
