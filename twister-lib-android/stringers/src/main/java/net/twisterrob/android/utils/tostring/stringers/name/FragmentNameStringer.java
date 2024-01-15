package net.twisterrob.android.utils.tostring.stringers.name;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.StringTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class FragmentNameStringer extends Stringer<Fragment> {
	public static final Stringer<Fragment> INSTANCE = new FragmentNameStringer();

	@Override public void toString(@NonNull ToStringAppender append, Fragment fragment) {
		if (fragment == null) {
			append.selfDescribingProperty(StringTools.NULL_STRING);
			return;
		}
		String mWho = ReflectionTools.get(fragment, "mWho");
		append.identity(StringTools.hashString(fragment), mWho);
	}
}
