package net.twisterrob.android.log

import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

// TODO revert to earlier state with val/vars https://youtrack.jetbrains.com/issue/KT-6653#focus=Comments-27-9920359.0-0
class AndroidLoggerServiceProvider : SLF4JServiceProvider {

	private val requestedApiVersion: String = "2.0.13"
	override fun getRequestedApiVersion(): String =
		requestedApiVersion

	private lateinit var loggerFactory: ILoggerFactory
	override fun getLoggerFactory(): ILoggerFactory =
		loggerFactory

	private lateinit var markerFactory: IMarkerFactory
	override fun getMarkerFactory(): IMarkerFactory =
		markerFactory

	private lateinit var mdcAdapter: MDCAdapter
	override fun getMDCAdapter(): MDCAdapter =
		mdcAdapter

	override fun initialize() {
		loggerFactory = AndroidLoggerFactory()
		markerFactory = BasicMarkerFactory()
		mdcAdapter = NOPMDCAdapter()
	}
}
