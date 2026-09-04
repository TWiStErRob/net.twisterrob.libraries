package net.twisterrob.orbit.logging

import org.orbitmvi.orbit.OrbitContainer
import org.slf4j.Logger

fun <S : Any, SE : Any> OrbitContainer<S, S, SE>.decorateLogging(log: Logger): OrbitContainer<S, S, SE> =
	LoggingContainerDecorator(this, OrbitSlf4jLogger(log))
