package net.twisterrob.test.hamcrest;

import java.util.Random;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class WrappingMatcherTest {

	@SuppressWarnings("unchecked")
	final Matcher<Object> mockWrappedMatcher = mock(Matcher.class);

	final WrappingMatcher<Object> sut = new WrappingMatcher<>(mockWrappedMatcher);

	@After public void tearDown() {
		verifyNoMoreInteractions(mockWrappedMatcher);
	}

	@Test public void testMatches() {
		Object fixtObject = new Object();
		boolean fixtMatches = new Random().nextBoolean();
		when(mockWrappedMatcher.matches(fixtObject)).thenReturn(fixtMatches);

		boolean result = sut.matches(fixtObject);

		assertEquals(fixtMatches, result);
		verify(mockWrappedMatcher).matches(fixtObject);
	}

	@Test public void testDescribeMismatch() {
		Object fixtObject = new Object();
		Description fixtDescription = mock(Description.class);

		sut.describeMismatch(fixtObject, fixtDescription);

		verify(mockWrappedMatcher).describeMismatch(fixtObject, fixtDescription);
	}

	@Test public void testDescribeTo() {
		Description fixtDescription = mock(Description.class);

		sut.describeTo(fixtDescription);

		verify(mockWrappedMatcher).describeTo(fixtDescription);
	}

	@Test public void testToString() {
		String fixtString = "toString";
		doReturn(fixtString).when(mockWrappedMatcher).toString();

		String result = sut.toString();

		assertEquals(fixtString, result);
		//verify(mockWrappedMatcher).toString(); // Not possible to verify toString.
	}

	@Ignore("Cannot do it, not possible to stub equals()")
	@Test public void testEqualsWrappingMatcher() {
		@SuppressWarnings("unchecked")
		Matcher<Object> mockOtherWrappedMatcher = mock(Matcher.class);
		WrappingMatcher<Object> otherWrappingMatcher =
				new WrappingMatcher<>(mockOtherWrappedMatcher);
		boolean fixtEquals = new Random().nextBoolean();
		doReturn(fixtEquals).when(mockWrappedMatcher).equals(mockOtherWrappedMatcher);

		boolean result = sut.equals(otherWrappingMatcher);

		assertEquals(fixtEquals, result);
		verify(mockWrappedMatcher).equals(mockOtherWrappedMatcher);
	}

	@Ignore("Cannot do it, not possible to stub equals()")
	@Test public void testEqualsSomethingElse() {
		Object fixtObject = new Object();
		boolean fixtEquals = new Random().nextBoolean();
		doReturn(fixtEquals).when(mockWrappedMatcher).equals(fixtObject);

		boolean result = sut.equals(fixtObject);

		assertEquals(fixtEquals, result);
		verify(mockWrappedMatcher).equals(fixtObject);
	}

	@Ignore("Cannot do it, not possible to stub hashCode()")
	@Test public void testHashCode() {
		int fixtHash = 12345678;
		doReturn(fixtHash).when(mockWrappedMatcher).hashCode();

		int result = sut.hashCode();

		assertEquals(fixtHash, result);
		verify(mockWrappedMatcher).hashCode();
	}
}
