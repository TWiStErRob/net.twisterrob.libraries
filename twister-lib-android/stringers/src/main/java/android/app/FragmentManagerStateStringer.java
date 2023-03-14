package android.app;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.ArrayTools;
import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

/**
 * @see FragmentManager for implementation of {@code FragmentManagerState}
 * which is default in the package, but behaves as @hide.
 */
public class FragmentManagerStateStringer extends Stringer<Object /*FragmentManagerState*/> {

	private final @NonNull ContentResolver contentResolver;

	public FragmentManagerStateStringer(@NonNull ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	@Override public void toString(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentManagerState*/ state) {
		if (isHiddenPolicyEnforced()) {
			append.identity(state, "FragmentManagerState <blocked>");
			return;
		}
		if (ReflectiveAccess.mBackStack != null) {
			Object[] backStack = ReflectionTools.get(state, ReflectiveAccess.mBackStack);
			append.beginSizedList("backstack", ArrayTools.safeLength(backStack), false);
			if (backStack != null) {
				for (Object /*BackStackState*/ bs : backStack) {
					append.item(bs);
				}
			}
			append.endSizedList();
		} else {
			append.item("backstack", "<blocked>");
		}
		if (ReflectiveAccess.mAdded != null) {
			int[] added = ReflectionTools.get(state, ReflectiveAccess.mAdded);
			append.item("added", added);
		} else {
			append.item("added", "<blocked>");
		}
		if (ReflectiveAccess.mActive != null) {
			Object[] active = ReflectionTools.get(state, ReflectiveAccess.mActive);
			append.beginSizedList("active fragments", ArrayTools.safeLength(active), false);
			if (active != null) {
				for (Object /*FragmentState*/ bs : active) {
					append.item(bs);
				}
			}
			append.endSizedList();
		} else {
			append.item("active fragments", "<blocked>");
		}
	}
	/**
	 * @see <a href="https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces">Docs</a>
	 */
	private boolean isHiddenPolicyEnforced() {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
				// Feature was added in Android 9 / API 28, so before that the policy is not enforced.
				return false;
			} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
				return Settings.Global.getInt(contentResolver, "hidden_api_policy_p_apps") > 1
						|| Settings.Global.getInt(contentResolver, "hidden_api_policy_pre_p_apps") > 1;
			} else {
				return Settings.Global.getInt(contentResolver, "hidden_api_policy") > 1;
			}
		} catch (Settings.SettingNotFoundException ex) {
			Logger LOG = LoggerFactory.getLogger(FragmentManagerStateStringer.class);
			LOG.error("Failed to read hidden API policy.", ex);
			return false;
		}
	}

	/**
	 * Access to this class has been blacklisted in API 29.
	 * ```
	 * Accessing hidden field Landroid/app/FragmentManagerState;->mBackStack:[Landroid/app/BackStackState; (blocked, reflection, denied)
	 * Accessing hidden field Landroid/app/FragmentManagerState;->mAdded:[I (blocked, reflection, denied)
	 * Accessing hidden field Landroid/app/FragmentManagerState;->mActive:[Landroid/app/FragmentState; (blocked, reflection, denied)
	 * ```
	 */
	private static class ReflectiveAccess {

		/**
		 * {@code @hide}, but loadable.
		 */
		private static final Class<?> FragmentManagerState =
				ReflectionTools.tryForName("android.app.FragmentManagerState");
		private static final Field mBackStack =
				ReflectionTools.tryFindDeclaredField(FragmentManagerState, "mBackStack");
		private static final Field mAdded =
				ReflectionTools.tryFindDeclaredField(FragmentManagerState, "mAdded");
		private static final Field mActive =
				ReflectionTools.tryFindDeclaredField(FragmentManagerState, "mActive");
	}
}
