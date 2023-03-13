package android.app;

import android.os.Bundle;

import androidx.annotation.NonNull;

import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;
import net.twisterrob.java.utils.tostring.stringers.DefaultStringer;

@SuppressWarnings("ConstantConditions")
public class FragmentStateStringer extends Stringer<Object /*FragmentState*/> {
	@Override public void toString(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentState*/ state) {
		//@SuppressWarnings("deprecation")
		//Fragment mInstance = ReflectionTools.get(state, "mInstance");
		//String mWho = ReflectionTools.get(mInstance, "mWho");
		int mIndex = ReflectionTools.get(state, "mIndex");
		String mClassName = ReflectionTools.get(state, "mClassName");

		append.identity(mIndex, DefaultStringer.shortenPackageNames(mClassName));
		append.beginPropertyGroup(null);
		{
			appendIdentity(append, state);
			appendFlags(append, state);
		}
		append.endPropertyGroup();
		appendDetails(append, state);
	}

	private void appendIdentity(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentState*/ state) {
		int mFragmentId = ReflectionTools.get(state, "mFragmentId");
		append.complexProperty("id", mFragmentId, ResourceNameStringer.INSTANCE);
		String mTag = ReflectionTools.get(state, "mTag");
		if (mTag != null) {
			append.rawProperty("tag", mTag);
		} else {
			int mContainerId = ReflectionTools.get(state, "mContainerId");
			append.complexProperty("container", mContainerId, ResourceNameStringer.INSTANCE);
		}
	}

	private void appendFlags(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentState*/ state) {
		boolean mFromLayout = ReflectionTools.get(state, "mFromLayout");
		boolean mRetainInstance = ReflectionTools.get(state, "mRetainInstance");
		boolean mDetached = ReflectionTools.get(state, "mDetached");

		append.booleanProperty(mFromLayout, "from layout");
		append.booleanProperty(mRetainInstance, "retained");
		append.booleanProperty(mDetached, "detached", "attached");
		appendNullDetails(append, state);
	}

	private void appendDetails(
			@NonNull ToStringAppender append, @NonNull Object /*FragmentState*/ state) {
		Bundle mArguments = ReflectionTools.get(state, "mArguments");
		if (mArguments != null) {
			append.item("Arguments", mArguments);
		}
		Bundle mSavedFragmentState = ReflectionTools.get(state, "mSavedFragmentState");
		if (mSavedFragmentState != null) {
			append.item("Saved instance state", mSavedFragmentState);
		}
	}

	private void appendNullDetails(ToStringAppender append,
			@NonNull Object /*FragmentState*/ state) {
		Bundle mArguments = ReflectionTools.get(state, "mArguments");
		if (mArguments == null) {
			append.rawProperty("args", null);
		}
		Bundle mSavedFragmentState = ReflectionTools.get(state, "mSavedFragmentState");
		if (mSavedFragmentState == null) {
			append.rawProperty("saved", null);
		}
	}
}
