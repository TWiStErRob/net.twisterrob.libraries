package net.twisterrob.android.test.junit.rules;

import org.junit.rules.ExternalResource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import net.twisterrob.android.test.DeviceUnlocker;

public class DeviceUnlockerRule extends ExternalResource {
	private final @NonNull DeviceUnlocker deviceUnlocker;

	public DeviceUnlockerRule() {
		this(InstrumentationRegistry.getInstrumentation().getContext());
	}

	public DeviceUnlockerRule(@NonNull Context context) {
		this(new DeviceUnlocker(context));
	}

	public DeviceUnlockerRule(@NonNull DeviceUnlocker deviceUnlocker) {
		this.deviceUnlocker = deviceUnlocker;
	}

	@Override protected void before() {
		deviceUnlocker.wakeUpWithDisabledKeyguard();
	}
}
