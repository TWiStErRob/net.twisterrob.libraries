package net.twisterrob.orbit.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import net.twisterrob.mockito.captureSingle
import net.twisterrob.orbit.logging.LoggingContainerDecorator.OrbitEvents
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestContainerHost.Companion.FQCN
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestContainerHost.Companion.matchesInner
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestContainerHost.Companion.matchesLambdaOf
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestEffect.TestEffect1
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestEffect.TestEffect2
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.matchesPattern
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito.mockingDetails
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.test.test
import kotlin.jvm.java

/**
 * @see LoggingContainerDecorator
 */
class LoggingContainerDecoratorTest {
	@Test
	fun testReduce() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			verifyNoInteractions(mockEvents)

			containerHost.testReduce()
			expectState(TestState(1))

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertThat(transformerStart::class.java.name, matchesInner("testReduce\$1"))

				val reducer = captureSingle {
					verify(mockEvents).reduce(eq(TestState(0)), capture(), eq(TestState(1)))
				}
				assertThat(reducer::class.java.name, matchesLambdaOf("testReduce\$1"))

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertThat(transformerEnd::class.java.name, matchesInner("testReduce\$1"))

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testSideEffect() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			verifyNoInteractions(mockEvents)

			containerHost.testSideEffect()
			expectSideEffect(TestEffect1)

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertThat(transformerStart::class.java.name, matchesInner("testSideEffect\$1"))

				verify(mockEvents).sideEffect(TestEffect1)

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertThat(transformerEnd::class.java.name, matchesInner("testSideEffect\$1"))

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testInline() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			verifyNoInteractions(mockEvents)

			containerHost.testInline()
			expectState(TestState(1))

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertThat(transformerStart::class.java.name, matchesInner("testInline\$1"))

				val reducer = captureSingle {
					verify(mockEvents).reduce(eq(TestState(0)), capture(), eq(TestState(1)))
				}
				assertThat(reducer::class.java.name, matchesLambdaOf("testInline\$1"))

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertThat(transformerEnd::class.java.name, matchesInner("testInline\$1"))

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testSubIntent() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			verifyNoInteractions(mockEvents)

			containerHost.testSubIntent()
			expectSideEffect(TestEffect1)
			expectSideEffect(TestEffect2)

			val events = listOf(
				"intentStarted" to "${FQCN}\$testSubIntent\$1",
				"intentStarted" to "${FQCN}\$testSubIntent\$1\$1",
				"sideEffect" to TestEffect1::class.java.name,
				"intentFinished" to "${FQCN}\$testSubIntent\$1\$1",
				"intentStarted" to "${FQCN}\$testSubIntent\$1\$2",
				"sideEffect" to TestEffect2::class.java.name,
				"intentFinished" to "${FQCN}\$testSubIntent\$1\$2",
				"intentFinished" to "${FQCN}\$testSubIntent\$1",
			)
			val calls = mockingDetails(mockEvents)
				.invocations
				.map { it.method.name to it.arguments.single() }
			assertEquals(events, calls.map { it.first to it.second::class.java.name })
			assertSame(calls.first().second, calls.last().second)
			assertSame(calls[1].second, calls[3].second)
			assertSame(calls[4].second, calls[6].second)
		}
	}

	@Test
	fun testSubIntentNested() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			verifyNoInteractions(mockEvents)

			containerHost.testSubIntentNested()
			expectSideEffect(TestEffect1)
			expectSideEffect(TestEffect2)

			val events = listOf(
				"intentStarted" to "${FQCN}\$testSubIntentNested\$1",
				"intentStarted" to "${FQCN}\$testSubIntentNested\$1\$1",
				"sideEffect" to TestEffect1::class.java.name,
				"intentStarted" to "${FQCN}\$testSubIntentNested\$1\$1\$1",
				"sideEffect" to TestEffect2::class.java.name,
				"intentFinished" to "${FQCN}\$testSubIntentNested\$1\$1\$1",
				"intentFinished" to "${FQCN}\$testSubIntentNested\$1\$1",
				"intentFinished" to "${FQCN}\$testSubIntentNested\$1",
			)
			val calls = mockingDetails(mockEvents)
				.invocations
				.map { it.method.name to it.arguments.single() }
			assertEquals(events, calls.map { it.first to it.second::class.java.name })
			assertSame(calls.first().second, calls.last().second)
			assertSame(calls[1].second, calls[6].second)
			assertSame(calls[3].second, calls[5].second)
		}
	}

	private data class TestState(val value: Int)
	private sealed interface TestEffect {
		data object TestEffect1 : TestEffect
		data object TestEffect2 : TestEffect
	}

	private class TestContainerHost(
		scope: CoroutineScope,
		events: OrbitEvents<TestState, TestEffect>,
	) : ContainerHost<TestState, TestEffect> {

		override val container =
			scope.container<TestState, TestEffect>(TestState(0))
				.decorateForTest(events)

		fun testReduce(): Job =
			intent {
				reduce {
					state.copy(value = state.value + 1)
				}
			}

		fun testSideEffect(): Job =
			intent {
				postSideEffect(TestEffect1)
			}

		fun testInline() {
			blockingIntent {
				reduce {
					state.copy(value = state.value + 1)
				}
			}
		}

		@OptIn(OrbitExperimental::class)
		fun testSubIntent() {
			intent {
				subIntent {
					postSideEffect(TestEffect1)
				}
				subIntent {
					postSideEffect(TestEffect2)
				}
			}
		}

		@OptIn(OrbitExperimental::class)
		fun testSubIntentNested() {
			intent {
				subIntent {
					postSideEffect(TestEffect1)
					subIntent {
						postSideEffect(TestEffect2)
					}
				}
			}
		}

		companion object {
			val FQCN: String = TestContainerHost::class.java.name

			fun matchesLambdaOf(signature: String): Matcher<String> {
				val pattern = """${Regex.escape(FQCN)}\$${Regex.escape(signature)}\$\$\QLambda\E(\$\d+)?/0x[0-9a-f]{16}"""
				return matchesPattern(Regex(pattern).toPattern())
			}

			fun matchesInner(signature: String): Matcher<String> =
				equalTo("${FQCN}\$${signature}")

			/**
			 * @see decorateLogging mimicking the real implementation, but with a mock listener.
			 */
			private fun <S : Any, SE : Any> Container<S, SE>.decorateForTest(events: OrbitEvents<S, SE>): Container<S, SE> =
				LoggingContainerDecorator(this, events)
		}
	}
}
