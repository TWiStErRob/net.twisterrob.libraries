package net.twisterrob.orbit.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import net.twisterrob.mockito.captureSingle
import net.twisterrob.orbit.logging.LoggingContainerDecorator.OrbitEvents
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestContainerHost.Companion.FQCN
import net.twisterrob.orbit.logging.LoggingContainerDecoratorTest.TestEffect.TestEffect1
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.test.test

/**
 * @see LoggingContainerDecorator
 */
class LoggingContainerDecoratorTest {
	@Test
	fun testReduce() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			expectInitialState()
			verifyNoInteractions(mockEvents)

			containerHost.testReduce()
			expectState(TestState(1))

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertEquals("${FQCN}\$testReduce\$1", transformerStart::class.java.name)

				val reducer = captureSingle {
					verify(mockEvents).reduce(eq(TestState(0)), capture(), eq(TestState(1)))
				}
				assertEquals("${FQCN}\$testReduce\$1\$1", reducer::class.java.name)

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertEquals("${FQCN}\$testReduce\$1", transformerEnd::class.java.name)

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testSideEffect() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			expectInitialState()
			verifyNoInteractions(mockEvents)

			containerHost.testSideEffect()
			expectSideEffect(TestEffect1)

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertEquals("${FQCN}\$testSideEffect\$1", transformerStart::class.java.name)

				verify(mockEvents).sideEffect(TestEffect1)

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertEquals("${FQCN}\$testSideEffect\$1", transformerEnd::class.java.name)

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testInline() = runTest {
		val mockEvents: OrbitEvents<TestState, TestEffect> = mock()
		TestContainerHost(backgroundScope, mockEvents).test(this) {
			expectInitialState()
			verifyNoInteractions(mockEvents)

			containerHost.testInline()
			expectState(TestState(1))

			inOrder(mockEvents) {
				val transformerStart = captureSingle {
					verify(mockEvents).intentStarted(capture())
				}
				assertEquals("${FQCN}\$testInline\$1", transformerStart::class.java.name)

				val reducer = captureSingle {
					verify(mockEvents).reduce(eq(TestState(0)), capture(), eq(TestState(1)))
				}
				assertEquals("${FQCN}\$testInline\$1\$1", reducer::class.java.name)

				val transformerEnd = captureSingle {
					verify(mockEvents).intentFinished(capture())
				}
				assertEquals("${FQCN}\$testInline\$1", transformerEnd::class.java.name)

				assertSame(transformerStart, transformerEnd)
				verifyNoMoreInteractions()
			}
		}
	}

	private data class TestState(val value: Int)
	private sealed interface TestEffect {
		data object TestEffect1 : TestEffect
	}

	private class TestContainerHost(
		scope: CoroutineScope,
		events: OrbitEvents<TestState, TestEffect>
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

		companion object {
			val FQCN: String = TestContainerHost::class.java.name

			/**
			 * @see decorateLogging mimicking the real implementation, but with a mock listener.
			 */
			private fun <S : Any, SE : Any> Container<S, SE>.decorateForTest(events: OrbitEvents<S, SE>): Container<S, SE> =
				LoggingContainerDecorator(this, events)
		}
	}
}
