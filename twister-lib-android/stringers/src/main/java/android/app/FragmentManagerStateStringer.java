package android.app;

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
	@Override public void toString(@NonNull ToStringAppender append, Object /*FragmentManagerState*/ state) {
		Object[] mBackStack = ReflectionTools.get(state, "mBackStack");
		append.beginSizedList("backstack", ArrayTools.safeLength(mBackStack), false);
		if (mBackStack != null) {
			for (Object /*BackStackState*/ bs : mBackStack) {
				append.item(bs);
			}
		}
		append.endSizedList();
		int[] mAdded = ReflectionTools.get(state, "mAdded");
		append.item("added", mAdded);
		Object[] mActive = ReflectionTools.get(state, "mActive");
		append.beginSizedList("active fragments", ArrayTools.safeLength(mActive), false);
		if (mActive != null) {
			for (Object /*FragmentState*/ bs : mActive) {
				append.item(bs);
			}
		}
		append.endSizedList();
	}
}
