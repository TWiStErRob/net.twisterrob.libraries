package net.twisterrob.android.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

open class PickForMimeType : ActivityResultContract<String, Uri?>() {
	override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
		intent.takeIf { resultCode == Activity.RESULT_OK }?.data

	override fun createIntent(context: Context, input: String): Intent =
		Intent(Intent.ACTION_PICK)
			.setType(input)
}
