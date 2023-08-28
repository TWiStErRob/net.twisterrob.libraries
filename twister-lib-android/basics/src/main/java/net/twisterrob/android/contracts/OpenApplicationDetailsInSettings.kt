package net.twisterrob.android.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

open class OpenApplicationDetailsInSettings : ActivityResultContract<String, Boolean>() {
	/**
	 * @param input package name
	 */
	@CallSuper override fun createIntent(
		context: Context,
		input: String
	): Intent =
		Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
			.setData(Uri.fromParts("package", input, null))
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
			.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

	override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
		resultCode == Activity.RESULT_OK
}
