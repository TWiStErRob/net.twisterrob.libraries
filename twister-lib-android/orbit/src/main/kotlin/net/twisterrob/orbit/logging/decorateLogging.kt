package net.twisterrob.orbit.logging

import org.orbitmvi.orbit.Container
import org.slf4j.Logger

fun <S : Any, SE : Any> Container<S, SE>.decorateLogging(log: Logger): Container<S, SE> =
	LoggingContainerDecorator(this, OrbitSlf4jLogger(log))
