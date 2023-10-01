package net.twisterrob.android.test.espresso.idle

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TestGlideActivity : AppCompatActivity() {
	lateinit var imageView: ImageView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		imageView = ImageView(this).apply {
			layoutParams = ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
			)
			id = IMAGE_VIEW_ID
		}
		setContentView(imageView)
	}

	companion object {
		val IMAGE_VIEW_ID: Int = View.generateViewId()
	}
}
