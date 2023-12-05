package org.slf4j.impl

import net.twisterrob.android.log.AndroidLoggerFactory
import org.slf4j.ILoggerFactory

@Suppress(
	// TODEL https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-6415928.0-0
	"ABSTRACT_MEMBER_NOT_IMPLEMENTED", "ACCIDENTAL_OVERRIDE", "NOTHING_TO_OVERRIDE",
	"DEPRECATION", // SLF4J 1.7.36 and 2.0.9 compatible at the same time.
	"unused", // SLF4J 1.x contract.
)
object StaticLoggerBinder : org.slf4j.spi.LoggerFactoryBinder {
	@JvmStatic
	val singleton: StaticLoggerBinder = this

	const val REQUESTED_API_VERSION: String = "1.7.36"

	override val loggerFactory: ILoggerFactory = AndroidLoggerFactory()
	override val loggerFactoryClassStr: String = loggerFactory::class.java.name
}
