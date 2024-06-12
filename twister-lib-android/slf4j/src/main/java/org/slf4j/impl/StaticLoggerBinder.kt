package org.slf4j.impl

import net.twisterrob.android.log.AndroidLoggerFactory
import org.slf4j.ILoggerFactory

// TODO revert to earlier state with val/vars https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-6415928.0-0
@Suppress(
	"DEPRECATION", // SLF4J 1.7.36 and 2.0.9 compatible at the same time.
	"unused", // SLF4J 1.x contract.
)
object StaticLoggerBinder : org.slf4j.spi.LoggerFactoryBinder {
	// Used by LoggerFactory as `StaticLoggerBinder.getSingleton()`.
	@JvmStatic
	val singleton: StaticLoggerBinder = this

	const val REQUESTED_API_VERSION: String = "1.7.36"

	private val loggerFactory: ILoggerFactory = AndroidLoggerFactory()
	override fun getLoggerFactory(): ILoggerFactory =
		loggerFactory

	private val loggerFactoryClassStr: String = loggerFactory::class.java.name
	override fun getLoggerFactoryClassStr(): String =
		loggerFactoryClassStr
}
