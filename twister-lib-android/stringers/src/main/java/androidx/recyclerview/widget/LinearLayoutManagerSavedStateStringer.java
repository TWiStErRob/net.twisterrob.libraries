package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager.SavedState;

import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

@DebugHelper
public class LinearLayoutManagerSavedStateStringer extends Stringer<SavedState> {
	@Override public void toString(@NonNull ToStringAppender append, SavedState state) {
		append.beginPropertyGroup("Anchor");
		append.rawProperty("pos", state.mAnchorPosition);
		append.rawProperty("offset", state.mAnchorOffset);
		append.rawProperty("fromEnd", state.mAnchorLayoutFromEnd);
		append.endPropertyGroup();
	}
}
