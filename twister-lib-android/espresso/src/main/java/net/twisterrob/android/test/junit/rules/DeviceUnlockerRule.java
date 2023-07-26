package net.twisterrob.android.test.junit.rules;

import org.junit.rules.ExternalResource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import net.twisterrob.android.test.DeviceUnlocker;

public class DeviceUnlockerRule extends ExternalResource {
	private final @NonNull DeviceUnlocker deviceUnlocker;
	private final boolean safeMode;

	public DeviceUnlockerRule() {
		this(false);
	}

	public DeviceUnlockerRule(boolean safeMode) {
		this(InstrumentationRegistry.getInstrumentation().getContext(), safeMode);
	}

	public DeviceUnlockerRule(@NonNull Context context, boolean safeMode) {
		this(new DeviceUnlocker(context), safeMode);
	}

	public DeviceUnlockerRule(@NonNull DeviceUnlocker deviceUnlocker, boolean safeMode) {
		this.deviceUnlocker = deviceUnlocker;
		this.safeMode = safeMode;
	}

	@Override protected void before() {
		if (!safeMode || deviceUnlocker.hasPermissions()) {
			deviceUnlocker.wakeUpWithDisabledKeyguard();
		}
	}
}
