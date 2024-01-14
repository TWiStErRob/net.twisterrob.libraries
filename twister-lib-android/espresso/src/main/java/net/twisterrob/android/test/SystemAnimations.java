package net.twisterrob.android.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

/**
 * Equivalent to:
 * <code><pre>
 * adb shell settings put global window_animation_scale 0
 * adb shell settings put global transition_animation_scale 0
 * adb shell settings put global animator_duration_scale 0
 * </pre></code>
 * but requires
 * <code>adb shell pm grant app.package.id {@value #ANIMATION_PERMISSION}</code>
 *
 * <p>
 * Usage:
 * <code><pre>
 * SystemAnimations anims = new SystemAnimations();
 * try {
 *     anims.backup();
 *     anims.disableAll();
 *     // do stuff that requires animations to be disabled
 * } finally {
 *     anims.restore();
 * }
 * </pre></code>
 * @see <a href="https://product.reverb.com/2015/06/06/disabling-animations-in-espresso-for-android-testing/">Disabling Animations</a>
 * @see <a href="https://gist.github.com/danielgomezrico/9371a79a7222a156ddad">Gist</a>
 */
@SuppressWarnings("JavaReflectionMemberAccess")
@SuppressLint({"DiscouragedPrivateApi", "PrivateApi", "SoonBlockedPrivateApi"}) // This class is meant to hack around.
public class SystemAnimations {
	private static final Logger LOG = LoggerFactory.getLogger(SystemAnimations.class);
	private static final String ANIMATION_PERMISSION = Manifest.permission.SET_ANIMATION_SCALE;
	private static final float DISABLED = 0.0f;
	private static final float DEFAULT = 1.0f;

	private static final @NonNull Class<?> windowManagerStubClass;
	private static final @NonNull Method asInterface;
	private static final @NonNull Class<?> serviceManagerClass;
	private static final @NonNull Method getService;
	private static final @NonNull Class<?> windowManagerClass;
	private static final @Nullable Method setAnimationScales;
	private static final @Nullable Method getAnimationScales;

	private static final @Nullable Method getWindowSession;
	private static final @Nullable Field sDurationScale;
	private static final @Nullable Method getDurationScale;
	private static final @Nullable Method setDurationScale;

	static {
		StrictMode.VmPolicy originalPolicy = StrictMode.getVmPolicy();
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(originalPolicy)
					.permitNonSdkApiUsage().build());
		}
		try {
			windowManagerStubClass = Class.forName("android.view.IWindowManager$Stub");
			asInterface = windowManagerStubClass.getDeclaredMethod("asInterface", IBinder.class);
			serviceManagerClass = Class.forName("android.os.ServiceManager");
			getService = serviceManagerClass.getDeclaredMethod("getService", String.class);
			windowManagerClass = Class.forName("android.view.IWindowManager");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
				setAnimationScales = windowManagerClass.getDeclaredMethod("setAnimationScales", float[].class);
				getAnimationScales = windowManagerClass.getDeclaredMethod("getAnimationScales");
			} else {
				setAnimationScales = null;
				getAnimationScales = null;
			}
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		} finally {
			StrictMode.setVmPolicy(originalPolicy);
		}
		StrictMode.VmPolicy originalPolicy2 = StrictMode.getVmPolicy();
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(originalPolicy2)
					.permitNonSdkApiUsage().build());
		}
		try {
			if (VERSION_CODES.JELLY_BEAN_MR1 < VERSION.SDK_INT) {
				Class<?> windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal");
				getWindowSession = windowManagerGlobalClass.getDeclaredMethod("getWindowSession");
			} else if (VERSION_CODES.JELLY_BEAN_MR1 == VERSION.SDK_INT) {
				Class<?> windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal");
				getWindowSession = windowManagerGlobalClass.getDeclaredMethod("getWindowSession", Looper.class);
			} else if (VERSION_CODES.ICE_CREAM_SANDWICH <= SDK_INT && SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
				Class<?> viewRootImplClass = Class.forName("android.view.ViewRootImpl");
				getWindowSession = viewRootImplClass.getDeclaredMethod("getWindowSession", Looper.class);
			} else if (VERSION_CODES.GINGERBREAD_MR1 < SDK_INT && SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH) {
				// No sources or devices available, so don't know.
				// It doesn't really matter anyway < JELLY_BEAN (16), because there's no ValueAnimator.sDurationScale.
				getWindowSession = null;
			} else {
				getWindowSession = null;
			}
			// ValueAnimator is since HONEYCOMB, but scaling was introduced in JELLY_BEAN
			// c38fa1f63674971f9ac6ced1a449fb81026b62f7: Add Developer Option setting for Animator scaling.
			if (VERSION_CODES.JELLY_BEAN == VERSION.SDK_INT) {
				sDurationScale = ValueAnimator.class.getDeclaredField("sDurationScale");
				sDurationScale.setAccessible(true);
				getDurationScale = SystemAnimations.class.getDeclaredMethod("getDurationScaleFillIn");
				setDurationScale = ValueAnimator.class.getDeclaredMethod("setDurationScale", Float.TYPE);
			} else if (VERSION_CODES.JELLY_BEAN < VERSION.SDK_INT) {
				sDurationScale = null; // not needed, we have a getter
				getDurationScale = ValueAnimator.class.getDeclaredMethod("getDurationScale");
				setDurationScale = ValueAnimator.class.getDeclaredMethod("setDurationScale", Float.TYPE);
			} else {
				sDurationScale = null;
				getDurationScale = null;
				setDurationScale = null;
			}
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		} finally {
			StrictMode.setVmPolicy(originalPolicy2);
		}
	}

	private final Object windowManager;
	private final boolean canSetAnimationScales;
	private final boolean canSetDurationScale;
	private float[] previousScales;

	public SystemAnimations(Context context) {
		try {
			IBinder windowManagerBinder = (IBinder)getService.invoke(null, Context.WINDOW_SERVICE);
			windowManager = asInterface.invoke(null, windowManagerBinder);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
		int permStatus = context.checkCallingOrSelfPermission(ANIMATION_PERMISSION);
		canSetAnimationScales = permStatus == PackageManager.PERMISSION_GRANTED
				&& setAnimationScales != null && getAnimationScales != null;
		canSetDurationScale = getDurationScale != null && setDurationScale != null;
		if (!canSetAnimationScales) {
			String resolution = canSetDurationScale
					? "using ValueAnimator hack as fallback"
					: "all operations will be no-op";
			LOG.warn("Application doesn't have {}, {}.", ANIMATION_PERMISSION, resolution);
		} else {
			LOG.trace("Application has {}, so changing system settings", ANIMATION_PERMISSION);
		}
	}

	public void backup() {
		previousScales = getScales();
	}

	@RequiresPermission(Manifest.permission.SET_ANIMATION_SCALE)
	public void restore() {
		setScales(previousScales);
	}

	@RequiresPermission(Manifest.permission.SET_ANIMATION_SCALE)
	public void disableAll() {
		setSystemAnimationsScale(DISABLED);
	}

	@RequiresPermission(Manifest.permission.SET_ANIMATION_SCALE)
	public void enableAll() {
		setSystemAnimationsScale(DEFAULT);
	}

	@RequiresPermission(Manifest.permission.SET_ANIMATION_SCALE)
	public void setSystemAnimationsScale(float animationScale) {
		try {
			float[] currentScales = getScales();
			Arrays.fill(currentScales, animationScale);
			setScales(currentScales);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod") // Contradicts lint:KotlinPropertyAccess.
	public void setScales(float[] currentScales) {
		if (canSetAnimationScales) {
			try {
				setAnimationScales.invoke(windowManager, new Object[] {currentScales});
			} catch (Throwable ex) {
				throw new IllegalStateException(ex);
			}
		} else if (canSetDurationScale) {
			try {
				preWindowSessionFix();
				setDurationScale.invoke(null, currentScales[0]);
			} catch (Throwable ex) {
				throw new IllegalStateException(ex);
			}
		}
	}
	public float[] getScales() {
		if (canSetAnimationScales) {
			try {
				return (float[])getAnimationScales.invoke(windowManager);
			} catch (Throwable ex) {
				throw new IllegalStateException(ex);
			}
		} else if (canSetDurationScale) {
			try {
				preWindowSessionFix();
				return new float[] {(Float)getDurationScale.invoke(null)};
			} catch (Throwable ex) {
				throw new IllegalStateException(ex);
			}
		} else {
			return new float[0];
		}
	}
	/**
	 * Calling {@code getWindowSession()} is required to ensure that the duration scale
	 * in {@link ValueAnimator} is read from the preferences first, so it doesn't overwrite our hacked value.
	 * If this method is not called before hacking the duration scale, the {@link android.view.ViewRootImpl} constructor
	 * will call it during {@link android.app.Activity#onResume onResume}.
	 * To see the exact call stack, add a field write breakpoint on {@code sDurationScale} and start an activity.
	 *
	 * @see android.view.WindowManagerGlobal#getWindowSession() how getWindowSession sets ValueAnimator.setDurationScale
	 * @see android.animation.ValueAnimator#sDurationScale
	 */
	@SuppressWarnings("JavadocReference")
	@TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
	private void preWindowSessionFix() throws IllegalAccessException, InvocationTargetException {
		if (getWindowSession != null) {
			if (VERSION_CODES.JELLY_BEAN_MR2 <= VERSION.SDK_INT) {
				getWindowSession.invoke(null);
			} else {
				getWindowSession.invoke(null, Looper.getMainLooper());
			}
		}
	}

	private static float getDurationScaleFillIn() throws IllegalAccessException {
		return (float)sDurationScale.get(null);
	}
}
