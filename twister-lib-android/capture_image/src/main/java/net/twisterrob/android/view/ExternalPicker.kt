package net.twisterrob.android.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.appcompat.widget.PopupMenu
import net.twisterrob.android.activity.CaptureImage
import net.twisterrob.android.capture_image.R
import net.twisterrob.android.contracts.PickForMimeType
import net.twisterrob.android.utils.tools.CameraTools
import net.twisterrob.android.utils.tools.TextTools
import net.twisterrob.android.utils.tools.asIterable

/**
 * Abstraction for handling for picking from external image source.
 * Currently implemented via a [PopupMenu].
 *
 * Note: "pick" is an overloaded term.
 *  * [ExternalPicker] and [CaptureImage.pick] means to import from external image sources.
 *  * [ActivityResultContracts.PickVisualMedia] / [MediaStore.ACTION_PICK_IMAGES] is a new way
 *    of selecting images via the Photo Picker.
 *  * [Intent.ACTION_PICK] is an old API for selecting anything from any other app.
 */
class ExternalPicker(
	activity: ComponentActivity,
	anchor: View,
	target: Uri,
	private val events: Events
) {
	private val context: Context
	private val menu: PopupMenu

	private val pickImage = ExplicitAbleActivityResultLauncher(
		activity,
		PickForMimeType(),
		"image/*",
		ActivityResultCallback { result: Uri? ->
			if (result != null) {
				events.onPick(result)
			} else {
				events.onCancelled()
			}
		}
	)

	private val getContent = ExplicitAbleActivityResultLauncher(
		activity,
		ActivityResultContracts.GetContent(),
		"image/*"
	) { result: Uri? ->
		if (result != null) {
			events.onGetContent(result)
		} else {
			events.onCancelled()
		}
	}

	@RequiresApi(Build.VERSION_CODES.KITKAT) // see isPhotoPickerAvailable
	private val pickVisualImage = ExplicitAbleActivityResultLauncher(
		activity,
		ActivityResultContracts.PickVisualMedia(),
		PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
	) { result: Uri? ->
		if (result != null) {
			events.onPickVisualImage(result)
		} else {
			events.onCancelled()
		}
	}

	private val captureImage = ExplicitAbleActivityResultLauncher(
		activity,
		ActivityResultContracts.TakePicture(),
		target
	) { result: Boolean? ->
		if (java.lang.Boolean.TRUE == result) {
			events.onCapture(target)
		} else {
			events.onCancelled()
		}
	}

	init {
		context = activity
		menu = createMenu(activity, anchor)
		menu.menu.apply {
			findItem(R.id.image__choose_external__pick).intent = pickImage.createIntent()
			findItem(R.id.image__choose_external__get).intent = getContent.createIntent()
			findItem(R.id.image__choose_external__visual).intent = pickVisualImage.createIntent()
			findItem(R.id.image__choose_external__capture).intent = captureImage.createIntent()
		}
		menu.setOnMenuItemClickListener { item: MenuItem ->
			// Clear the dismiss listener, so it doesn't get called after this method returns.
			// We only want to call it when the user cancels the menu, not when they select something.
			menu.setOnDismissListener(null)
			events.itemSelected()
			when {
				item.itemId == R.id.image__choose_external__get
					|| item.groupId == R.id.image__choose_external__get_group -> {
					getContent.launch(item.intent!!)
					true
				}
				item.itemId == R.id.image__choose_external__pick
					|| item.groupId == R.id.image__choose_external__pick_group -> {
					pickImage.launch(item.intent!!)
					true
				}
				item.itemId == R.id.image__choose_external__capture
					|| item.groupId == R.id.image__choose_external__capture_group -> {
					captureImage.launch(item.intent!!)
					true
				}
				item.itemId == R.id.image__choose_external__visual
					|| item.groupId == R.id.image__choose_external__visual_group -> {
					pickVisualImage.launch(item.intent!!)
					true
				}
				else -> {
					error("Unknown menu item: ${item}")
				}
			}
		}
	}

	fun show() {
		// Set dismiss listener right before showing to make sure it exists.
		// It might be cleared when an item is selected. See #setOnMenuItemClickListener.
		menu.setOnDismissListener { events.onCancelled() }
		populateGroups(menu.menu)
		menu.show()
	}

	private fun populateGroups(menu: Menu) {
		menu.add(R.id.image__choose_external__pick_group, R.id.image__choose_external__pick)
		menu.add(R.id.image__choose_external__get_group, R.id.image__choose_external__get)
		menu.add(R.id.image__choose_external__visual_group, R.id.image__choose_external__visual)
		menu.add(R.id.image__choose_external__capture_group, R.id.image__choose_external__capture)
		menu.asIterable().forEach {
			it.icon = it.icon?.fixIcon(it.isHeading, context.resources)
			if (it.isHeading) {
				it.title = TextTools.bold(it.title)
			}
		}
		menu.showCapture(CameraTools.canLaunchCameraIntent(context))
		menu.showPickVisual(isPhotoPickerAvailable(context))
	}

	interface Events {
		fun onCancelled()
		fun itemSelected()
		fun onGetContent(result: Uri)
		fun onPick(result: Uri)
		fun onPickVisualImage(result: Uri)
		fun onCapture(result: Uri)
	}
}

private class ExplicitAbleActivityResultLauncher<I>(
	private val context: Context,
	private val contract: ActivityResultContract<I, *>,
	private val input: I,
	private val launcher: ActivityResultLauncher<Intent>,
) {

	/**
	 * Create a generic intent for the [contract] and [input].
	 */
	fun createIntent(): Intent =
		contract.createIntent(context, input)

	/**
	 * Launch the generic intent generated by [createIntent],
	 * or launch a package-specific explicit intent (overridden component) from the same.
	 */
	fun launch(input: Intent) {
		launcher.launch(input)
	}

	companion object {
		// Cannot be a constructor because of generic parameters.
		operator fun <C, I, O> invoke(
			context: C,
			contract: ActivityResultContract<I, O>,
			input: I,
			callback: ActivityResultCallback<O>
		): ExplicitAbleActivityResultLauncher<I>
			where C : Context, C : ActivityResultCaller =
			ExplicitAbleActivityResultLauncher(
				context,
				contract,
				input,
				context.registerForActivityResult(
					ActivityResultContracts.StartActivityForResult()
				) {
					callback.onActivityResult(contract.parseResult(it.resultCode, it.data))
				}
			)
	}
}

private fun createMenu(activity: Activity, anchor: View): PopupMenu =
	PopupMenu(activity, anchor).apply {
		gravity = Gravity.TOP or Gravity.START
		setForceShowIcon(true) // See fixIcons().
		inflate(R.menu.image__choose_external)
	}

/**
 * Types of menu items:
 *  * Headings are not in a group, they're just above them.
 *  * Groups are represented in the [R.menu.image__choose_external] XML.
 *   They're populated by [add] and they have `groupId` set.
 *  * There's no third type yet, so being lazy with [Boolean].
 */
private val MenuItem.isHeading: Boolean
	get() = this.groupId == Menu.NONE

private fun Menu.add(@IdRes groupId: Int, @IdRes headerId: Int) {
	val header = this.findItem(headerId) ?: error("Menu item not found for id: ${headerId}")
	this.addIntentOptions(groupId, Menu.NONE, header.order + 1, null, null, header.intent, 0, null)
	when (this.asIterable().count { it.groupId == groupId }) {
		0 -> {
			header.isVisible = false
		}
		1 -> {
			header.isVisible = true
			//header.collapseFromSingletonGroup(this, groupId)
		}
		else -> {
			header.isVisible = true
		}
	}
}

@Suppress("unused") // Keep for now.
private fun MenuItem.collapseFromSingletonGroup(menu: Menu, groupId: Int) {
	val item = menu.asIterable().single { it.groupId == groupId }
	icon = item.icon
	menu.removeGroup(groupId)
}

private fun Menu.showCapture(canCapture: Boolean) {
	this.findItem(R.id.image__choose_external__capture).isVisible = canCapture
	this.setGroupVisible(R.id.image__choose_external__capture_group, canCapture)
}

private fun Menu.showPickVisual(hasPhotoPicker: Boolean) {
	this.findItem(R.id.image__choose_external__visual).isVisible = hasPhotoPicker
	this.setGroupVisible(R.id.image__choose_external__visual_group, hasPhotoPicker)
}

private fun isPhotoPickerAvailable(context: Context): Boolean =
	if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT)
		ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)
	else
		false

/**
 * Ensure all drawables have the same size, otherwise they're misaligned.
 * Also add some indentation to resolved package listings, to give some hierarchy without use of submenus.
 *
 * [PopupMenu.show] via [androidx.appcompat.view.menu.MenuPopupHelper]
 * instantiates [androidx.appcompat.view.menu.StandardMenuPopup]
 * which inflates [androidx.appcompat.R.layout.abc_popup_menu_item_layout]
 * using [androidx.appcompat.view.menu.MenuAdapter].
 * Items in the adapter will be [androidx.appcompat.view.menu.ListMenuItemView]
 * and their icons are added in [androidx.appcompat.view.menu.ListMenuItemView.insertIconView]
 * from [androidx.appcompat.R.layout.abc_list_menu_item_icon].
 */
private fun Drawable.fixIcon(heading: Boolean, resources: Resources): Drawable {
	val indent = if (heading) 0 else
		resources.getDimensionPixelSize(R.dimen.image__choose_external__menu_icon_indent)
	val size = resources.getDimensionPixelSize(
		if (heading)
			R.dimen.image__choose_external__menu_icon_header_size
		else 
			R.dimen.image__choose_external__menu_icon_intent_size
	)
	return object : DrawableWrapperCompat(this) {
		// Adding indent reserves extra width, because ImageView will take this size.
		override fun getIntrinsicWidth(): Int = size + indent
		override fun getIntrinsicHeight(): Int = size
		override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
			// Shift left by `indent`, so the icon gets back to it's `size` width, and the left side of it will be empty.
			super.setBounds(left + indent, top, right, bottom)
		}
	}
}
