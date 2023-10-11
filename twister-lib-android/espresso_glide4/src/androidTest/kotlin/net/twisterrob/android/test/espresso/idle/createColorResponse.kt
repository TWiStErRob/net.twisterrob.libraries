package net.twisterrob.android.test.espresso.idle

import android.graphics.Bitmap
import androidx.annotation.ColorInt
import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import org.junit.Assert.assertEquals

fun createColorResponse(color: Int): MockResponse =
	MockResponse().setBody(createPNGResponse(createColorImage(color)))

private fun createPNGResponse(bitmap: Bitmap): Buffer =
	Buffer().apply {
		outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
	}

/**
 * @param color the AARRGGBB color to write,
 * be careful with [alpha](https://stackoverflow.com/a/13794128/253468) if you read the PNG back.
 */
private fun createColorImage(@ColorInt color: Int, width: Int = 1, height: Int = 1): Bitmap {
	val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
		setHasAlpha(true)
		isPremultiplied = false
		eraseColor(color)
	}
	assertEquals("Sanity check", color, bitmap.getPixel(0, 0))
	return bitmap
}
