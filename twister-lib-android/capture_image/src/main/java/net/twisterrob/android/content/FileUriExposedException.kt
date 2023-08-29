package net.twisterrob.android.content

/**
 * @see android.os.FileUriExposedException
 */
internal class FileUriExposedException(message: String) : RuntimeException(message) {
	companion object {
		private const val serialVersionUID = -1789299731318366566L
	}
}
