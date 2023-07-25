package net.twisterrob.android.test;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

/**
 * The below should be equivalent to using this class,
 * but it's better to achieve everything without requiring an activity.
 * <code><pre>
 * getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
 * getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
 * getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 * </pre></code>
 */
public class DeviceUnlocker {
	private final @NonNull Context context;
	private final @NonNull KeyguardManager keyguardManager;
	private final @NonNull PowerManager powerManager;

	public DeviceUnlocker(@NonNull Context context) {
		this.context = context;
		this.keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		this.powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	}

	public boolean hasPermissions() {
		return hasPermission(Manifest.permission.DISABLE_KEYGUARD)
				&& hasPermission(Manifest.permission.WAKE_LOCK);
	}

	private boolean hasPermission(String permission) {
		return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
	}

	@RequiresPermission(allOf = {
			Manifest.permission.DISABLE_KEYGUARD,
			Manifest.permission.WAKE_LOCK
	})
	@SuppressWarnings("deprecation")
	public void wakeUpWithDisabledKeyguard() {
		KeyguardManager.KeyguardLock kl = keyguardManager.newKeyguardLock("net.twisterrob::keyguard_off_for_test");
		kl.disableKeyguard();
		try {
			wakeUp();
		} finally {
			kl.reenableKeyguard();
		}
	}

	@RequiresPermission(Manifest.permission.WAKE_LOCK)
	@SuppressWarnings("deprecation")
	public void wakeUp() {
		int flags = PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE;
		WakeLock wakeLock = powerManager.newWakeLock(flags, "net.twisterrob::wake_up_for_test");
		try {
			wakeLock.acquire(1000L /* 1 second */);
		} finally {
			wakeLock.release();
		}
	}
}
