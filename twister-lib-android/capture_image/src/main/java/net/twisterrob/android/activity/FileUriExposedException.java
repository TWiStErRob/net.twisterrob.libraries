package net.twisterrob.android.activity;

/**
 * @see android.os.FileUriExposedException
 */
class FileUriExposedException extends RuntimeException {
	private static final long serialVersionUID = -1789299731318366566L;

	public FileUriExposedException(String message) {
		super(message);
	}
}
