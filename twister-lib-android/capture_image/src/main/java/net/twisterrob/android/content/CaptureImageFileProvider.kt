package net.twisterrob.android.content

import android.content.Context
import android.content.pm.ProviderInfo
import android.net.Uri
import androidx.core.content.FileProvider
import net.twisterrob.android.capture_image.R
import net.twisterrob.android.utils.tools.AndroidTools
import net.twisterrob.android.utils.tools.IOTools
import net.twisterrob.android.utils.tools.allowThreadDiskReads
import net.twisterrob.android.utils.tools.allowThreadDiskWrites
import java.io.File
import java.io.IOException

class CaptureImageFileProvider : FileProvider() {

	override fun attachInfo(context: Context, info: ProviderInfo) {
		allowThreadDiskReads {
			// > StrictMode policy violation; ~duration=27 ms: android.os.strictmode.DiskReadViolation
			// > at java.io.File.exists(File.java:813)
			// > at android.app.ContextImpl.ensurePrivateDirExists(ContextImpl.java:759)
			// > at android.app.ContextImpl.ensurePrivateCacheDirExists(ContextImpl.java:755)
			// > at android.app.ContextImpl.getCacheDir(ContextImpl.java:866)
			// > at android.content.ContextWrapper.getCacheDir(ContextWrapper.java:322)
			// > at androidx.core.content.FileProvider.parsePathStrategy(FileProvider.java:712)
			// > at androidx.core.content.FileProvider.getPathStrategy(FileProvider.java:645)
			// > at androidx.core.content.FileProvider.attachInfo(FileProvider.java:424)
			// > at android.app.ActivityThread.installProvider(ActivityThread.java:7508)
			// > at android.app.ActivityThread.installContentProviders(ActivityThread.java:7019)
			// > at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6790)
			// > at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2132)
			// > at android.os.Handler.dispatchMessage(Handler.java:106)
			// > at android.os.Looper.loop(Looper.java:288)
			// > at android.app.ActivityThread.main(ActivityThread.java:7918)
			// > at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
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
			allowThreadDiskWrites {
				// FileProvider.getPathStrategy -> Context.getCacheDir -> ensure -> File.exists
				return getUriForFile(context, provider.authority, file)
			}
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
