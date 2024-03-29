package net.twisterrob.android.utils.tostring.stringers.name;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

import static net.twisterrob.android.AndroidConstants.INVALID_RESOURCE_ID;

public class ResourceNameStringer extends Stringer<Integer> {
	public static /*final*/ Stringer<Integer> INSTANCE;

	// TODO figure out a way to utilize this @see BundleStringer
	private static final Collection<String> RESOLVE_RESOURCE_ID_KEYS = new HashSet<>(Arrays.asList(
			// savedInstanceState > android:viewHierarchyState
			"android:views",
			// (FragmentManagerState)android:support:fragments > FragmentManagerImpl.VIEW_STATE_TAG
			"android:view_state",
			// savedInstanceState > NavigationView.SavedState
			"android:menu:action_views",
			// Activity > savedInstanceState > android:viewHierarchyState
			"android:focusedViewId"
	));

	private final Context context;
	private final Resources resources;
	public ResourceNameStringer(Context context) {
		this.context = context.getApplicationContext();
		this.resources = context.getResources();
	}

	@Override public String getType(Integer object) {
		return null;
	}
	@Override public void toString(@NonNull ToStringAppender append, Integer object) {
		int id = object; // force un-box
		append.selfDescribingProperty(shortenName(getName(id)));
	}
	private String shortenName(String name) {
		if (name.startsWith(context.getPackageName())) {
			name = "app" + name.substring(context.getPackageName().length());
		}
		return name;
	}

	/**
	 * -1 will be resolved to {@code "NO_ID"}, 0 is {@code "invalid"}, everything else will be tried to be resolved.
	 * @see View#NO_ID
	 * @see Resources#getIdentifier
	 * @return Fully qualified name of the resource,
	 *         or special values: {@code "View.NO_ID"}, {@code "invalid"}, {@code "not-found::<id>"}
	 */
	private String getName(int id) {
		if (id == View.NO_ID) {
			return "View.NO_ID";
		} else if (id == INVALID_RESOURCE_ID) {
			return "invalid";
		} else {
			try {
				return resources.getResourceName(id);
			} catch (Resources.NotFoundException ignore) {
				return "not-found::" + id;
			}
		}
	}
}
