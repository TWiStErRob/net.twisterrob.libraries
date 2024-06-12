package net.twisterrob.orbit.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import net.twisterrob.orbit.logging.DecorateLoggingIntegrationTest.TestEffect.TestEffect1
import net.twisterrob.orbit.logging.DecorateLoggingIntegrationTest.TestEffect.TestEffect2
import net.twisterrob.orbit.logging.DecorateLoggingIntegrationTest.TestEffect.TestEffect3
import org.junit.Test
import org.mockito.ArgumentMatchers.matches
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.test.test
import org.slf4j.Logger
import java.util.regex.Pattern

/**
 * @see decorateLogging
 * @see LoggingContainerDecorator
 * @see OrbitSlf4jLogger
 */
@Suppress("LoggingSimilarMessage") // Testing log messages.
class DecorateLoggingIntegrationTest {
	private val logger: Logger = mock()
	//private val logger: Logger = spy(org.slf4j.LoggerFactory.getLogger("test")) // If want to see output.

	@Test
	fun testSideEffect() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.sideEffect()
			expectSideEffect(TestEffect1)

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$sideEffect\$1",
					emptyMap<String, Any?>(),
				)
				verify(logger).trace("postSideEffect({})", TestEffect1)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$sideEffect\$1",
					emptyMap<String, Any?>(),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testNoParams() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.intentNoParams()
			expectSideEffect(TestEffect1)

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentNoParams\$1",
					emptyMap<String, Any?>(),
				)
				verify(logger).trace("postSideEffect({})", TestEffect1)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentNoParams\$1",
					emptyMap<String, Any?>(),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testUnusedParams() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.intentUnusedParams(42, "str")
			expectSideEffect(TestEffect1)

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentUnusedParams\$1",
					emptyMap<String, Any?>(),
				)
				verify(logger).trace("postSideEffect({})", TestEffect1)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentUnusedParams\$1",
					emptyMap<String, Any?>(),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testIntentWithParams() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.intentWithParams(42, "str")
			expectSideEffect(TestEffect1)

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentWithParams\$1",
					mapOf("intParam" to 42, "stringParam" to "str"),
				)
				verify(logger).trace("postSideEffect({})", TestEffect1)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$intentWithParams\$1",
					mapOf("intParam" to 42, "stringParam" to "str"),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testReduceWithParams() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.reduceWithParams(42, "str")
			expectState(TestState(value = 1))

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$reduceWithParams\$1",
					mapOf("intParam" to 42, "stringParam" to "str"),
				)
				verify(logger).trace(
					eq("reduced via {}:\n{}\n->\n{}"),
					matches(lambdaIn("DecorateLoggingIntegrationTest\$TestContainerHost\$reduceWithParams\$1")),
					eq(TestState(value = 0)),
					eq(TestState(value = 1)),
				)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$reduceWithParams\$1",
					mapOf("intParam" to 42, "stringParam" to "str"),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testNestedIntent() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.nestedIntent()
			expectSideEffect(TestEffect1)
			expectSideEffect(TestEffect3)
			expectSideEffect(TestEffect2)

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$nestedIntent\$1",
					emptyMap<String, Any?>(),
				)
				verify(logger).trace("postSideEffect({})", TestEffect1)
				verify(logger).trace("postSideEffect({})", TestEffect3)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$nestedIntent\$1",
					emptyMap<String, Any?>(),
				)

				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$nestedIntent\$1\$1",
					emptyMap<String, Any?>(),
				)
				verify(logger).trace("postSideEffect({})", TestEffect2)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$nestedIntent\$1\$1",
					emptyMap<String, Any?>(),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	@Test
	fun testInlineOrbitBlockingIntent() = runTest {
		TestContainerHost(backgroundScope, logger).test(this) {
			expectInitialState()
			containerHost.inlineOrbit(42)
			expectState(TestState(value = 42))

			inOrder(logger) {
				verify(logger).trace(
					"Starting intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$inlineOrbit\$1",
					mapOf("value" to 42),
				)
				verify(logger).trace(
					eq("reduced via {}:\n{}\n->\n{}"),
					matches(lambdaIn("DecorateLoggingIntegrationTest\$TestContainerHost\$inlineOrbit\$1")),
					eq(TestState(value = 0)),
					eq(TestState(value = 42)),
				)
				verify(logger).trace(
					"Finished intent: {} with {}",
					"DecorateLoggingIntegrationTest\$TestContainerHost\$inlineOrbit\$1",
					mapOf("value" to 42),
				)
				verifyNoMoreInteractions()
			}
		}
	}

	private data class TestState(val value: Int)
	private sealed interface TestEffect {
		data object TestEffect1 : TestEffect
		data object TestEffect2 : TestEffect
		data object TestEffect3 : TestEffect
	}

	private class TestContainerHost(
		scope: CoroutineScope,
		logger: Logger
	) : ContainerHost<TestState, TestEffect> {

		override val container =
			scope.container<TestState, TestEffect>(TestState(value = 0))
				.decorateLogging(logger)

		fun sideEffect(): Job =
			intent {
				postSideEffect(TestEffect1)
			}

		fun intentNoParams(): Job =
			intent {
				postSideEffect(TestEffect1)
			}

		@Suppress("UNUSED_PARAMETER")
		fun intentUnusedParams(intParam: Int, stringParam: String): Job =
			intent {
				postSideEffect(TestEffect1)
			}

		fun intentWithParams(intParam: Int, stringParam: String): Job =
			intent {
				stringParam + intParam // Just use them inside the lambda.
				postSideEffect(TestEffect1)
			}

		fun reduceWithParams(intParam: Int, stringParam: String): Job =
			intent {
				reduce {
					stringParam + intParam // Just use them inside the lambda.
					state.copy(value = state.value + 1)
				}
			}

		fun nestedIntent(): Job =
			intent {
				postSideEffect(TestEffect1)
				intent {
					postSideEffect(TestEffect2)
				}
				postSideEffect(TestEffect3)
			}

		fun inlineOrbit(value: Int) {
			blockingIntent {
				reduce {
					state.copy(value = value)
				}
			}
		}
	}
}

private fun lambdaIn(name: String): Pattern =
	Regex("""${Regex.escape(name)}\$\$\QLambda\E\$\d+/0x[0-9a-f]{16}""").toPattern()
