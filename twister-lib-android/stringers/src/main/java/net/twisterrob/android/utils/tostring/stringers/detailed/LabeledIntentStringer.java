package net.twisterrob.android.utils.tostring.stringers.detailed;

import android.content.pm.LabeledIntent;
import android.content.res.Resources;

import net.twisterrob.java.utils.tostring.ToStringAppender;
import net.twisterrob.java.utils.tostring.stringers.DefaultStringer;

public class LabeledIntentStringer extends IntentStringer<LabeledIntent> {
	@Override public String getType(LabeledIntent object) {
		return DefaultStringer.debugType(object);
	}

	@Override protected void extraProperties(ToStringAppender append, LabeledIntent intent) {
		if (intent.getSourcePackage() != null) {
			append.item("mSourcePackage", intent.getSourcePackage());
		}
		if (intent.getLabelResource() != Resources.ID_NULL) {
			append.item("mLabelRes", intent.getLabelResource());
		}
		if (intent.getNonLocalizedLabel() != null) {
			append.item("mNonLocalizedLabel", intent.getNonLocalizedLabel());
		}
		if (intent.getIconResource() != Resources.ID_NULL) {
			append.item("mIcon", intent.getIconResource());
		}
	}
}
