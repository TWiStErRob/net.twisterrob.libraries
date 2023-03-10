package net.twisterrob.android.utils.tostring.stringers.detailed.activitymanager;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Build.*;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.*;

public class MemoryInfoStringer extends Stringer<ActivityManager.MemoryInfo> {
	@Override public void toString(@NonNull ToStringAppender append, MemoryInfo info) {
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			append.measuredProperty("total", "bytes", info.totalMem);
		}
		append.measuredProperty("available", "bytes", info.availMem);
		append.measuredProperty("low threshold", "bytes", info.threshold);
		append.booleanProperty(info.lowMemory, "low on memory", "plenty memory");
	}
}
