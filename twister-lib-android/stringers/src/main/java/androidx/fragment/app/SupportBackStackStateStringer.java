package androidx.fragment.app;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class SupportBackStackStateStringer extends Stringer<BackStackState> {

	@Override public void toString(@NonNull ToStringAppender append, BackStackState state) {
		append.beginSizedList("fragments", state.mFragments.size());
		for (String fragment : state.mFragments) {
			append.item(fragment);
		}
		append.endSizedList();

		append.beginSizedList("transactions", state.mTransactions.size());
		for (BackStackRecordState transaction : state.mTransactions) {
			append.item(transaction);
		}
		append.endSizedList();
	}
}
