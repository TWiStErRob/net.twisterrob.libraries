package net.twisterrob.android.log

import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

// TODEL https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-6415928.0-0
@Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "ACCIDENTAL_OVERRIDE", "NOTHING_TO_OVERRIDE")
class AndroidLoggerServiceProvider : SLF4JServiceProvider {

	override val requestedApiVersion: String = "2.0.9"

	override lateinit var loggerFactory: ILoggerFactory
		private set

	override lateinit var markerFactory: IMarkerFactory
		private set

	override lateinit var mdcAdapter: MDCAdapter
		private set

	override fun initialize() {
		loggerFactory = AndroidLoggerFactory()
		markerFactory = BasicMarkerFactory()
		mdcAdapter = NOPMDCAdapter()
	}
}
