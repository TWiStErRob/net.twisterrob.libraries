package net.twisterrob.android.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SettingsContracts {

	class OpenApplicationDetails extends ActivityResultContract<String, Boolean> {

		@CallSuper
		@Override public @NonNull Intent createIntent(
				@NonNull Context context,
				@NonNull String packageName
		) {
			return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
					.setData(Uri.fromParts("package", packageName, null))
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
					.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		}

		@Override public @NonNull Boolean parseResult(int resultCode, @Nullable Intent intent) {
			return resultCode == Activity.RESULT_OK;
		}
	}
}
