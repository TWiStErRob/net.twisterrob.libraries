package org.slf4j.impl

import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory

// TODO revert to earlier state with val/vars https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-9920359.0-0
@Suppress(
	"DEPRECATION", // SLF4J 1.7.36 and 2.0.9 compatible at the same time.
	"unused", // SLF4J 1.x contract.
)
object StaticMarkerBinder : org.slf4j.spi.MarkerFactoryBinder {
	// Used by MarkerFactory (1.x) as `StaticMarkerBinder.getSingleton()`.
	@JvmStatic
	val singleton: StaticMarkerBinder = this

	private val markerFactory: IMarkerFactory = BasicMarkerFactory()
	override fun getMarkerFactory(): IMarkerFactory =
		markerFactory

	private val markerFactoryClassStr: String = markerFactory::class.java.name
	override fun getMarkerFactoryClassStr(): String =
		markerFactoryClassStr
}
