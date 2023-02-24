package net.twisterrob.android.test.espresso;

import org.junit.Rule;

import net.twisterrob.android.test.junit.TestPackageIntentRule;
import net.twisterrob.inventory.android.test.activity.TestActivity;

public class DialogMatchersTest_CloseDialogAndroid extends DialogMatchersTest_CloseDialog {

	@SuppressWarnings("deprecation")
	@Rule public final androidx.test.rule.ActivityTestRule<TestActivity> activity =
			new TestPackageIntentRule<>(TestActivity.class);

	public DialogMatchersTest_CloseDialogAndroid(
			boolean positive, boolean negative, boolean neutral, boolean cancellable, boolean expectedClosed) {
		super(positive, negative, neutral, cancellable, expectedClosed);
	}

	@Override protected void showDialog() {
		DialogMatchersTest.showAndroidAlertDialog(activity.getActivity(), positive, negative, neutral, cancellable);
	}
}
