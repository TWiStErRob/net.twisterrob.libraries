package androidx.fragment.app;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static androidx.lifecycle.Lifecycle.State;

import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class SupportBackStackRecordStateStringer extends Stringer<BackStackRecordState> {

	@Override public void toString(@NonNull ToStringAppender append, BackStackRecordState state) {
		append.identity(state.mIndex, state.mName);
		append.selfDescribingProperty(TransitionCommand.Converter.toString(state.mTransition));
		append.booleanProperty(state.mReorderingAllowed, "reordering allowed",
				"reordering disallowed");
		appendBreadCrumb(append, state);
		appendShared(append, state);
		appendOps(append, state);
	}

	/**
	 * Based on how {@link BackStackRecordState#mOps} is written and read.
	 * @see BackStackRecordState#mOps
	 * @see BackStackRecordState#BackStackRecordState(BackStackRecord)
	 * @see BackStackRecordState#instantiate(FragmentManager)
	 */
	private void appendOps(ToStringAppender append, BackStackRecordState state) {
		int size = state.mOps.length / OpsRef.OPS_LENGTH;
		if (size > 1) {
			append.beginSizedList("ops", size);
		}
		int opsPos = 0;
		int opPos = 0;
		while (opsPos < state.mOps.length) {
			String who = state.mFragmentWhos.get(opPos);
			State oldMaxState = State.values()[state.mOldMaxLifecycleStates[opPos]];
			State newMaxState = State.values()[state.mCurrentMaxLifecycleStates[opPos]];
			append.item(
					new OpsRef(who, oldMaxState, newMaxState, state.mOps, opsPos),
					OpsRefStringer.INSTANCE
			);
			opsPos += OpsRef.OPS_LENGTH;
			opPos += 1;
		}
		if (size > 1) {
			append.endSizedList();
		}
		assert opsPos == state.mOps.length;
	}

	private static void appendShared(ToStringAppender append, BackStackRecordState state) {
		append.beginPropertyGroup("shared");
		if (state.mSharedElementSourceNames != null && state.mSharedElementTargetNames != null) {
			int sharedNamesCount = state.mSharedElementSourceNames.size();
			for (int i = 0; i < sharedNamesCount; i++) {
				String source = state.mSharedElementSourceNames.get(i);
				String target = state.mSharedElementTargetNames.get(i);
				append.item(source, target);
			}
		}
		append.endPropertyGroup();
	}

	@SuppressWarnings("unused")
	private void appendBreadCrumb(ToStringAppender append, BackStackRecordState state) {
		// mBreadCrumbTitleRes, mBreadCrumbTitleText is deprecated
		// mBreadCrumbShortTitleRes, mBreadCrumbShortTitleText is deprecated
	}

	private static class OpsRef {
		/**
		 * How many {@code pos++} there are in {@link BackStackRecordState} constructor.
		 */
		static final int OPS_LENGTH = 6;
		@SuppressWarnings({"FieldCanBeLocal", "unused"})
		private final int pos;

		private final @Nullable String who;
		private final @NonNull State oldMaxState;
		private final @NonNull State newMaxState;
		private final @TransactionOperationCommand int cmd;
		private final int fromExpandedOp;
		private final @AnimRes int enterAnim;
		private final @AnimRes int exitAnim;
		private final @AnimRes int popEnterAnim;
		private final @AnimRes int popExitAnim;

		/**
		 * Reversal of {@code for} loop in {@link BackStackRecordState#BackStackRecordState(BackStackRecord)}.
		 */
		public OpsRef(
				@Nullable String who,
				@NonNull State oldMaxState,
				@NonNull State newMaxState,
				@NonNull int[] mOps,
				int startPos
		) {
			this.who = who;
			this.oldMaxState = oldMaxState;
			this.newMaxState = newMaxState;
			int currentPos = startPos;
			this.pos = startPos;
			this.cmd = mOps[currentPos++];
			this.fromExpandedOp = mOps[currentPos++];
			this.enterAnim = mOps[currentPos++];
			this.exitAnim = mOps[currentPos++];
			this.popEnterAnim = mOps[currentPos++];
			this.popExitAnim = mOps[currentPos++];
			assert currentPos == startPos + OPS_LENGTH;
		}
	}

	private static class OpsRefStringer extends Stringer<OpsRef> {
		static final Stringer<OpsRef> INSTANCE = new OpsRefStringer();
		@Override public String getType(OpsRef object) {
			return null;
		}
		@Override public void toString(@NonNull ToStringAppender append, OpsRef op) {
			String commandString = TransactionOperationCommand.Converter.toString(op.cmd);
			append.identity(op.who, commandString);
			append.booleanProperty(op.fromExpandedOp == 1, "from expanded op");
			if (op.oldMaxState == op.newMaxState) {
				append.rawProperty("maxLifecycle", op.newMaxState);
			} else {
				append.beginPropertyGroup("MaxLifecycle");
				{
					append.rawProperty("old", op.oldMaxState);
					append.rawProperty("new", op.newMaxState);
				}
				append.endPropertyGroup();
			}
			append.beginPropertyGroup("Anim");
			{
				append.complexProperty("enter", op.enterAnim, ResourceNameStringer.INSTANCE);
				append.complexProperty("exit", op.exitAnim, ResourceNameStringer.INSTANCE);
				append.complexProperty("popEnter", op.popEnterAnim, ResourceNameStringer.INSTANCE);
				append.complexProperty("popExit", op.popExitAnim, ResourceNameStringer.INSTANCE);
			}
			append.endPropertyGroup();
		}
	}
}
