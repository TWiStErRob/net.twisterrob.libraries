package net.twisterrob.android.test.espresso;

import org.junit.Rule;

import androidx.appcompat.app.AlertDialog;

import net.twisterrob.android.test.junit.TestPackageIntentRule;
import net.twisterrob.inventory.android.test.activity.TestActivityCompat;

public class DialogMatchersTest_CloseDialogCompat extends DialogMatchersTest_CloseDialog {

	@SuppressWarnings("deprecation")
	@Rule public final androidx.test.rule.ActivityTestRule<TestActivityCompat> activity =
			new TestPackageIntentRule<>(TestActivityCompat.class);

	public DialogMatchersTest_CloseDialogCompat(
			boolean positive, boolean negative, boolean neutral, boolean cancellable, boolean expectedClosed) {
		super(positive, negative, neutral, cancellable, expectedClosed);
	}

	@Override protected Runnable showDialog() {
		final AlertDialog dialog = DialogMatchersTest.showAppCompatAlertDialog(
				activity.getActivity(), positive, negative, neutral, cancellable
		);
		return new Runnable() {
			@Override public void run() {
				dialog.dismiss();
			}
		};
	}
}
