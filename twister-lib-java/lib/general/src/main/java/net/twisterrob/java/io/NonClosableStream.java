package net.twisterrob.java.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NonClosableStream extends FilterInputStream {
	private boolean closeAttempted;
	public NonClosableStream(InputStream in) {
		super(in);
	}

	public boolean isCloseAttempted() {
		return closeAttempted;
	}

	@Override public void close() {
		closeAttempted = true;
		//super.close(); // don't allow it
	}

	public void doClose() throws IOException {
		super.close();
	}
}
