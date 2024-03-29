package net.twisterrob.test.hamcrest;

import java.io.FileNotFoundException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.rules.TestName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThrows;

import static net.twisterrob.test.hamcrest.Matchers.hasMessage;

public class StackTraceMatcherTest {

	@Rule public final TestName name = new TestName();

	@Test public void testPasses() {
		FileNotFoundException ex = new FileNotFoundException("test");
		assertThat(ex, StackTraceMatcher.hasStackTrace(MatcherTestHelpers.TRUE_MATCHER));
	}

	@Test public void testFailureMessage() {
		final FileNotFoundException ex = new FileNotFoundException("test");
		AssertionError expectedFailure = assertThrows(AssertionError.class, new ThrowingRunnable() {
			@Override public void run() {
				assertThat(ex, StackTraceMatcher.hasStackTrace(MatcherTestHelpers.FALSE_MATCHER));
			}
		});

		//Expected: false matcher
		//but: didn't match stack trace of java.io.FileNotFoundException: test
		//at net.twisterrob.test.hamcrest.StackTraceMatcherTest.testFailureMessage(StackTraceMatcherTest.java:46)
		assertThat(expectedFailure, hasMessage(allOf(
				containsString("didn't match stack trace of"),
				containsString(getClass().getName() + "." + name.getMethodName()) // stack trace of creation
		)));
	}
}
