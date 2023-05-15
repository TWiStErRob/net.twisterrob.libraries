package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.android.annotation.ServiceStartFlag;
import net.twisterrob.android.annotation.ServiceStartResult;
import net.twisterrob.android.annotation.TrimMemoryLevel;
import net.twisterrob.android.utils.log.LoggingDebugProvider.LoggingHelper;
import net.twisterrob.android.utils.tools.StringerTools;
import net.twisterrob.java.annotations.DebugHelper;

import static net.twisterrob.android.utils.log.LoggingDebugProvider.returned;

@DebugHelper
@SuppressLint("Registered") // allow registration if wanted without needing to subclass
@Deprecated @SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class LoggingJobIntentService extends androidx.core.app.JobIntentService {
	private static final Logger LOG = LoggerFactory.getLogger("JobIntentService");

	public LoggingJobIntentService() {
		log("<ctor>");
	}

	@Override public void onCreate() {
		log("onCreate");
		super.onCreate();
	}
	@Deprecated @SuppressWarnings({"deprecation", "RedundantSuppression"})
	@Override public void onStart(@Nullable Intent intent, int startId) {
		log("onStart", intent, startId);
		super.onStart(intent, startId);
	}
	@Override public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
		String flagsString = ServiceStartFlag.Converter.toString(flags);
		log("onStartCommand", intent, flagsString, startId);
		int startResult = super.onStartCommand(intent, flags, startId);
		String returnString = ServiceStartResult.Converter.toString(startResult);
		logReturn("onStopCurrentWork", returnString, intent, flagsString, startId);
		return startResult;
	}
	@Override public void onDestroy() {
		log("onDestroy");
		super.onDestroy();
	}
	@Override public @Nullable IBinder onBind(@NonNull Intent intent) {
		log("onBind", intent);
		IBinder binder = super.onBind(intent);
		logReturn("onBind", binder, intent);
		return binder;
	}
	@Override public void onConfigurationChanged(Configuration newConfig) {
		log("onConfigurationChanged", newConfig);
		super.onConfigurationChanged(newConfig);
	}
	@Override public void onLowMemory() {
		log("onLowMemory");
		super.onLowMemory();
	}
	@Override public void onTrimMemory(@TrimMemoryLevel int level) {
		log("onTrimMemory", StringerTools.toTrimMemoryString(level));
		super.onTrimMemory(level);
	}
	@Override public boolean onUnbind(Intent intent) {
		log("onUnbind", intent);
		boolean wantRebind = super.onUnbind(intent);
		logReturn("onUnbind", wantRebind, intent);
		return wantRebind;
	}
	@Override public void onRebind(Intent intent) {
		log("onRebind", intent);
		super.onRebind(intent);
	}
	@Override public void onTaskRemoved(Intent rootIntent) {
		log("onTaskRemoved", rootIntent);
		super.onTaskRemoved(rootIntent);
	}
	@Override public boolean onStopCurrentWork() {
		log("onStopCurrentWork");
		boolean reschedule = super.onStopCurrentWork();
		logReturn("onStopCurrentWork", reschedule);
		return reschedule;
	}
	@Override protected void onHandleWork(@NonNull Intent intent) {
		log("onHandleWork", intent);
	}

	private void log(@NonNull String method, @NonNull Object... args) {
		LoggingHelper.log(LOG, getName(), method, null, args);
	}
	private void logReturn(@NonNull String method, @Nullable Object ret, @NonNull Object... args) {
		LoggingHelper.log(LOG, getName(), method, returned(ret), args);
	}
	private @NonNull String getName() {
		return StringerTools.toNameString(this);
	}
}
