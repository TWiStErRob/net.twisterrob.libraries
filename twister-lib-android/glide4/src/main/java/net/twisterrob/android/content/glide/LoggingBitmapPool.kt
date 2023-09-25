package net.twisterrob.android.content.glide;

import org.slf4j.*;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import androidx.annotation.NonNull;

import net.twisterrob.android.annotation.TrimMemoryLevel;
import net.twisterrob.android.utils.tools.StringerTools;

public class LoggingBitmapPool implements BitmapPool {
	private static final @NonNull Logger LOG = LoggerFactory.getLogger("glide.BitmapPool");
	private final @NonNull BitmapPool wrapped;

	public LoggingBitmapPool(@NonNull BitmapPool wrapped) {
		this.wrapped = wrapped;
	}

	@Override public long getMaxSize() {
		long result = wrapped.getMaxSize();
		LOG.trace("getMaxSize(): {}", result);
		return result;
	}
	@Override public void setSizeMultiplier(float sizeMultiplier) {
		LOG.trace("setSizeMultiplier({})", sizeMultiplier);
		wrapped.setSizeMultiplier(sizeMultiplier);
	}
	@Override public void put(Bitmap bitmap) {
		LOG.trace("put({})", StringerTools.toString(bitmap));
		wrapped.put(bitmap);
	}
	@Override public @NonNull Bitmap get(int width, int height, Config config) {
		Bitmap result = wrapped.get(width, height, config);
		LOG.trace("get({}, {}, {}): {}", width, height, config, StringerTools.toString(result));
		return result;
	}
	@Override public @NonNull Bitmap getDirty(int width, int height, Config config) {
		Bitmap result = wrapped.getDirty(width, height, config);
		LOG.trace("getDirty({}, {}, {}): {}", width, height, config, StringerTools.toString(result));
		return result;
	}
	@Override public void clearMemory() {
		LOG.trace("clearMemory()");
		wrapped.clearMemory();
	}
	@Override public void trimMemory(@TrimMemoryLevel int level) {
		LOG.trace("trimMemory({})", StringerTools.toTrimMemoryString(level));
		wrapped.trimMemory(level);
	}
}
