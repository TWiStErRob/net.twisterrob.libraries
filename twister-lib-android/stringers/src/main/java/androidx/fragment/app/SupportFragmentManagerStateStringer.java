package androidx.fragment.app;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.ArrayTools;
import net.twisterrob.java.utils.CollectionTools;
import net.twisterrob.java.utils.tostring.*;


public class SupportFragmentManagerStateStringer extends Stringer<FragmentManagerState> {
	@Override public void toString(@NonNull ToStringAppender append, FragmentManagerState state) {
		append.beginSizedList("backstack", ArrayTools.safeLength(state.mBackStack), false);
		if (state.mBackStack != null) {
			for (BackStackRecordState record : state.mBackStack) {
				append.item(record);
			}
		}
		append.endSizedList();
		append.item("added", state.mAdded);
		append.beginSizedList("active fragments", CollectionTools.safeSize(state.mActive), false);
		if (state.mActive != null) {
			for (String active : state.mActive) {
				append.item(active);
			}
		}
		append.endSizedList();
	}
}
