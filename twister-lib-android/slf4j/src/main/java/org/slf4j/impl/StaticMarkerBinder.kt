package org.slf4j.impl

import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory

@Suppress(
	// TODEL https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-6415928.0-0
	"ABSTRACT_MEMBER_NOT_IMPLEMENTED", "ACCIDENTAL_OVERRIDE", "NOTHING_TO_OVERRIDE",
	"DEPRECATION", // SLF4J 1.7.36 and 2.0.9 compatible at the same time.
	"unused", // SLF4J 1.x contract.
)
object StaticMarkerBinder : org.slf4j.spi.MarkerFactoryBinder {
	@JvmStatic
	val singleton: StaticMarkerBinder = this

	override val markerFactory: IMarkerFactory = BasicMarkerFactory()
	override val markerFactoryClassStr: String = markerFactory::class.java.name
}
