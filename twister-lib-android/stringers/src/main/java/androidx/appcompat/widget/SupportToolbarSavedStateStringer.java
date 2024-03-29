package androidx.appcompat.widget;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar.SavedState;

import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

@DebugHelper
public class SupportToolbarSavedStateStringer extends Stringer<SavedState> {
	@Override public void toString(@NonNull ToStringAppender append, SavedState state) {
		append.booleanProperty(state.isOverflowOpen, "Overflow open", "Overflow closed");
		append.complexProperty("Expanded MenuItem", state.expandedMenuItemId, ResourceNameStringer.INSTANCE);
	}
}
