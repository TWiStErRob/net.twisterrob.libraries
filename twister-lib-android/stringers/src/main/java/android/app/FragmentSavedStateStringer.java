package android.app;

import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

@RequiresApi(VERSION_CODES.HONEYCOMB_MR2)
@SuppressWarnings("deprecation")
public class FragmentSavedStateStringer extends Stringer<Fragment.SavedState> {
	@Override public String getType(Fragment.SavedState object) {
		return "Fragment.SavedState";
	}
	@Override public void toString(@NonNull ToStringAppender append, Fragment.SavedState state) {
		// mState is package private, but probably @hide too, because it's not accessible
		append.item(ReflectionTools.get(state, "mState"));
	}
}
