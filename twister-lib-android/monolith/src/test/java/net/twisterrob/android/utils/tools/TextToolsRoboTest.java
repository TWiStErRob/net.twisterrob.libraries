package net.twisterrob.android.utils.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

import android.text.SpannableStringBuilder;

@RunWith(RobolectricTestRunner.class)
public class TextToolsRoboTest {

	@Test public void join_does_not_append_separator_when_no_input() {
		List<CharSequence> input = Collections.emptyList();

		SpannableStringBuilder result = TextTools.join(" ", input);

		assertEquals("", result.toString());
	}

	@Test public void join_does_not_append_separator_when_one_string() {
		List<CharSequence> input = Collections.singletonList("abc");

		SpannableStringBuilder result = TextTools.join(" ", input);

		assertEquals("abc", result.toString());
	}

	@Test public void join_separates_inputs_by_separator() {
		List<CharSequence> input = Arrays.asList("a", "b", "c");

		SpannableStringBuilder result = TextTools.join(" ", input);

		assertEquals("a b c", result.toString());
	}
}
