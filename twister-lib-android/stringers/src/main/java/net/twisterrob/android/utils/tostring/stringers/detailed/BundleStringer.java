package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.lang.reflect.Array;

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
			if (value != null 
					&& value.getClass().isArray()
					&& !value.getClass().getComponentType().isPrimitive()) {
				int length = Array.getLength(value);
				append.beginSizedList(key, length);
				for (int i = 0; i < length; i++) {
					append.item(i, Array.get(value, i));
				}
				append.endSizedList();
			} else {
				append.item(key, value);
			}
		}
		append.endSizedList();
	}
}
