package net.twisterrob.android.content.glide;

import java.security.MessageDigest;

import com.bumptech.glide.load.Key;

import androidx.annotation.NonNull;

public class LongSignature implements Key {
	private final long signature;

	public LongSignature() {
		this(System.currentTimeMillis());
	}

	public LongSignature(long signature) {
		super();
		if (signature == 0) {
			throw new IllegalStateException("0 signature, is the some data missing?");
		}
		this.signature = signature;
	}

	@Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
		messageDigest.update(String.valueOf(signature).getBytes(CHARSET));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LongSignature that = (LongSignature)o;

		return signature == that.signature;
	}

	@Override public int hashCode() {
		return Long.hashCode(signature);
	}

	@Override public @NonNull String toString() {
		return "LongSignature(" + signature + ")";
	}
}
