package net.twisterrob.android.contracts

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

@RequiresApi(VERSION_CODES.KITKAT)
open class OpenOpenableDocument : ActivityResultContracts.OpenDocument() {
	/**
	 * @param input [Intent.EXTRA_MIME_TYPES]
	 */
	override fun createIntent(context: Context, input: Array<String>): Intent =
		super
			.createIntent(context, input)
			.addCategory(Intent.CATEGORY_OPENABLE)
}
