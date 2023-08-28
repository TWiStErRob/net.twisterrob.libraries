package net.twisterrob.android.content

import android.content.Context
import android.content.pm.ProviderInfo
import android.net.Uri
import androidx.core.content.FileProvider
import net.twisterrob.android.capture_image.R
import net.twisterrob.android.utils.tools.AndroidTools
import net.twisterrob.android.utils.tools.IOTools
import net.twisterrob.android.utils.tools.allowThreadDiskReads
import java.io.File
import java.io.IOException

class CaptureImageFileProvider : FileProvider() {

	override fun attachInfo(context: Context, info: ProviderInfo) {
		allowThreadDiskReads {
			super.attachInfo(context, info)
		}
	}

	companion object {
		/**
		 * Warning: this is used inlined in [R.xml.image__capture_paths],
		 * because `@path` doesn't support string resources.
		 */
		private const val PUBLIC_CAPTURE_IMAGE_FOLDER_NAME = "capture_image"

		@JvmStatic
		fun getUriForFile(context: Context, file: File): Uri {
			val provider = AndroidTools.findProviderAuthority(context, CaptureImageFileProvider::class.java)
			return getUriForFile(context, provider.authority, file)
		}

		@JvmStatic
		fun getTempFile(context: Context, path: String): File {
			val temp = context.cacheDir.resolve(PUBLIC_CAPTURE_IMAGE_FOLDER_NAME)
			try {
				IOTools.ensure(temp)
			} catch (ex: IOException) {
				throw IllegalStateException(
					"Cannot create directory ${PUBLIC_CAPTURE_IMAGE_FOLDER_NAME} in cache",
					ex
				)
			}
			return temp.resolve(path)
		}
	}
}
