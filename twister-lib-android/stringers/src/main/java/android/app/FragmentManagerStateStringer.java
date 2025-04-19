package android.app;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import net.twisterrob.java.utils.ArrayTools;
import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.StringTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

/**
 * @see FragmentManager for implementation of {@code FragmentManagerState}
 * which is default in the package, but behaves as @hide.
 *
 * Access to {@code FragmentManagerState} has been blacklisted in API 29.
 * ```
 * Accessing hidden field Landroid/app/FragmentManagerState;->mBackStack:[Landroid/app/BackStackState; (blocked, reflection, denied)
 * Accessing hidden field Landroid/app/FragmentManagerState;->mAdded:[I (blocked, reflection, denied)
 * Accessing hidden field Landroid/app/FragmentManagerState;->mActive:[Landroid/app/FragmentState; (blocked, reflection, denied)
 * ```
 */
public class FragmentManagerStateStringer extends Stringer<Object /*FragmentManagerState*/> {

	private final @NonNull ContentResolver contentResolver;

	public FragmentManagerStateStringer(@NonNull ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	@Override public void toString(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentManagerState*/ state) {
		if (isHiddenPolicyEnforced()) {
			append.identity(StringTools.hashString(state), "<blocked>");
			return;
		}
		Object[] backStack = ReflectionTools.get(state, "mBackStack");
		append.beginSizedList("backstack", ArrayTools.safeLength(backStack), false);
		if (backStack != null) {
			for (Object /*BackStackState*/ bs : backStack) {
				append.item(bs);
			}
		}
		append.endSizedList();
		int[] added = ReflectionTools.get(state, "mAdded");
		append.item("added", added);
		Object[] active = ReflectionTools.get(state, "mActive");
		append.beginSizedList("active fragments", ArrayTools.safeLength(active), false);
		if (active != null) {
			for (Object /*FragmentState*/ bs : active) {
				append.item(bs);
			}
		}
		append.endSizedList();
	}

	/**
	 * @see <a href="https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces">Docs</a>
	 */
	private boolean isHiddenPolicyEnforced() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
			// Feature was added in Android 9 / API 28, so before that the policy is not enforced.
			return false;
		} else {
			try {
				if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
					return setting("hidden_api_policy_p_apps") > 1
							|| setting("hidden_api_policy_pre_p_apps") > 1;
				} else {
					return setting("hidden_api_policy") > 1;
				}
			} catch (Settings.SettingNotFoundException ex) {
				// If the setting doesn't exist (default), then the policy is enforced.
				return true;
			}
		}
	}

	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private int setting(String name) throws Settings.SettingNotFoundException {
		return Settings.Global.getInt(contentResolver, name);
	}
}
