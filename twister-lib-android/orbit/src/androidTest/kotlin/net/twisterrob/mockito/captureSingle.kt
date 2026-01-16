package net.twisterrob.mockito

import org.mockito.kotlin.KArgumentCaptor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <reified T> captureSingle(verify: KArgumentCaptor<T>.() -> Unit): T {
	contract {
		callsInPlace(verify, EXACTLY_ONCE)
	}
	val captor = KArgumentCaptor<T>(T::class)
	captor.verify()
	return captor.allValues.single()
}
