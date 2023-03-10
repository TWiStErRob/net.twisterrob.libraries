package net.twisterrob.android.utils.tostring.stringers.detailed;

import android.os.Bundle;

import androidx.annotation.NonNull;

import net.twisterrob.android.utils.tools.BundleTools;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.collections.NullsSafeComparator;
import net.twisterrob.java.utils.CollectionTools;
import net.twisterrob.java.utils.tostring.*;

@DebugHelper
public class BundleStringer extends Stringer<Bundle> {
	@Override public String getType(Bundle object) {
		return null;
	}
	@Override public void toString(@NonNull ToStringAppender append, Bundle bundle) {
		append.beginSizedList(bundle, bundle.size());
		for (String key : CollectionTools.newTreeSet(bundle.keySet(), new NullsSafeComparator<String>())) {
			Object value = BundleTools.getObject(bundle, key);
			append.item(key, value);
		}
		append.endSizedList();
	}
}
