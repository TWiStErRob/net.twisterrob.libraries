package net.twisterrob.android.log

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.slf4j.LoggerFactory

class AndroidLoggerServiceProviderTest {

	@Test
	fun test() {
		val logger = LoggerFactory.getLogger("MyLogs")

		assertNotNull(logger)
		assertEquals(AndroidLogger::class, logger::class)
	}
}
