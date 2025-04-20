package net.twisterrob.android.log

import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidLoggerTest {
	@Test
	fun logsMessages() {
		val logger = AndroidLogger("My Long Name", "My Short Name", "MyTag")

		logger.trace("My trace Message")
		logger.debug("My debug Message")
		logger.info("My info Message")
		logger.warn("My warn Message")
		logger.error("My error Message")

		assertEquals(
			listOf(
				"V/MyTag   : My trace Message",
				"D/MyTag   : My debug Message",
				"I/MyTag   : My info Message",
				"W/MyTag   : My warn Message",
				"E/MyTag   : My error Message",
			),
			getLogsFor("MyTag"),
		)
	}
}
