package org.slf4j.impl

import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.spi.MDCAdapter

@Suppress(
	"unused", "MemberVisibilityCanBePrivate", // SLF4J 1.x contract.
)
object StaticMDCBinder {
	@JvmStatic
	val singleton: StaticMDCBinder = this

	val MDCA: MDCAdapter = NOPMDCAdapter()
	val MDCAdapterClassStr: String = MDCA::class.java.name
}
