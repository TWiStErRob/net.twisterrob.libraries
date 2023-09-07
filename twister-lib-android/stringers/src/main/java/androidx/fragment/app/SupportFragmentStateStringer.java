package androidx.fragment.app;

import androidx.annotation.NonNull;

import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;
import net.twisterrob.java.utils.tostring.stringers.DefaultStringer;

/**
 * androidx.fragment 1.5.5 to 1.6.1 upgrade removed
 * <ul>
 *     <li>{@code val mArguments: Bundle?}</li>
 *     <li>{@code var mSavedFragmentState: Bundle?}</li>
 * </ul>
 */
public class SupportFragmentStateStringer extends Stringer<FragmentState> {
	@Override public void toString(@NonNull ToStringAppender append, FragmentState state) {
		append.identity(state.mWho, DefaultStringer.shortenPackageNames(state.mClassName));
		append.beginPropertyGroup(null);
		{
			appendIdentity(append, state);
			appendFlags(append, state);
		}
		append.endPropertyGroup();
	}

	private void appendIdentity(ToStringAppender append, FragmentState state) {
		append.complexProperty("id", state.mFragmentId, ResourceNameStringer.INSTANCE);
		if (state.mTag != null) {
			append.rawProperty("tag", state.mTag);
		} else {
			append.complexProperty("container", state.mContainerId, ResourceNameStringer.INSTANCE);
		}
	}

	private void appendFlags(ToStringAppender append, FragmentState state) {
		append.booleanProperty(state.mFromLayout, "from layout");
		append.booleanProperty(state.mRetainInstance, "retained");
		append.booleanProperty(state.mDetached, "detached", "attached");
		append.booleanProperty(state.mHidden, "hidded");
		append.booleanProperty(state.mRemoving, "removing");
	}
}
