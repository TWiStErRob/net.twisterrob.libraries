package androidx.fragment.app;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.*;

import static net.twisterrob.java.utils.ArrayTools.*;

public class SupportFragmentManagerStateStringer extends Stringer<FragmentManagerState> {
	@Override public void toString(@NonNull ToStringAppender append, FragmentManagerState state) {
		append.beginSizedList("backstack", safeLength(state.mBackStack), false);
		if (state.mBackStack != null) {
			for (BackStackState bs : state.mBackStack) {
				append.item(bs);
			}
		}
		append.endSizedList();
		append.item("added", state.mAdded);
		append.beginSizedList("active fragments", safeLength(state.mActive), false);
		if (state.mActive != null) {
			for (FragmentState bs : state.mActive) {
				append.item(bs);
			}
		}
		append.endSizedList();
	}
}
