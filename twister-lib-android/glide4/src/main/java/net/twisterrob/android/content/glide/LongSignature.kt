package net.twisterrob.android.content.glide

import com.bumptech.glide.load.Key
import java.security.MessageDigest

class LongSignature @JvmOverloads constructor(
	private val signature: Long = System.currentTimeMillis()
) : Key {

	init {
		check(signature != 0L) { "0 signature, is some data missing?" }
	}

	override fun updateDiskCacheKey(messageDigest: MessageDigest) {
		messageDigest.update(signature.toString().toByteArray(Key.CHARSET))
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other == null || this::class.java != other::class.java) {
			return false
		}
		val that = other as LongSignature
		return signature == that.signature
	}

	override fun hashCode(): Int =
		signature.hashCode()

	override fun toString(): String =
		"LongSignature(${signature})"
}
