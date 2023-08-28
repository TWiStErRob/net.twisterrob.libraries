package net.twisterrob.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.PickVisualMediaRequestKt;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat;
import androidx.appcompat.widget.PopupMenu;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.content.ImageRequest;
import net.twisterrob.android.utils.tools.ViewTools;

/**
 * Abstraction for handling for picking from external image source.
 * Currently implemented via a {@link PopupMenu}.
 * <p> 
 * Note: "pick" is an overloaded term.
 * <ul>
 *     <li>"External Picker" and "pick" in CaptureImage activity means to import from external image sources.</li>
 *     <li>PickVisualMedia / MediaStore.ACTION_PICK_IMAGES is a new way of selecting images via the Photo Picker.</li>
 *     <li>Intent.ACTION_PICK is an old API for selecting anything from any other app.</li>
 * </ul>
 */
public class ExternalPicker {

	private final @NonNull Context context;
	private final @NonNull Events events;
	private final @NonNull PopupMenu menu;

	public ExternalPicker(
			@NonNull ComponentActivity activity,
			@NonNull View anchor,
			@NonNull Uri target,
			@NonNull Events events
	) {
		this.context = activity;
		this.events = events;
		ExplicitAbleActivityResultLauncher<PickVisualMediaRequest> pickImage = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.PickVisualMedia(), // STOPSHIP real pick?
				PickVisualMediaRequestKt.PickVisualMediaRequest(
						ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE
				),
				(@Nullable Uri result) -> {
					if (result != null) {
						events.onPick(result);
					} else {
						events.onCancelled();
					}
				}
		);
		ExplicitAbleActivityResultLauncher<String> getContent = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.GetContent(),
				"image/*",
				(@Nullable Uri result) -> {
					if (result != null) {
						events.onGetContent(result);
					} else {
						events.onCancelled();
					}
				}
		);
		ExplicitAbleActivityResultLauncher<Uri> captureImage = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.TakePicture(),
				target,
				(@Nullable Boolean result) -> {
					if (Boolean.TRUE.equals(result)) {
						events.onCapture(target);
					} else {
						events.onCancelled();
					}
				}
		);
		menu = createMenu(activity, anchor);
		menu.getMenu().findItem(R.id.image__choose_external__get).setIntent(getContent.createIntent());
		menu.getMenu().findItem(R.id.image__choose_external__pick).setIntent(pickImage.createIntent());
		menu.getMenu().findItem(R.id.image__choose_external__capture).setIntent(captureImage.createIntent());
		menu.setOnMenuItemClickListener((@NonNull MenuItem item) -> {
			// Clear the dismiss listener, so it doesn't get called after this method returns.
			// We only want to call it when the user cancels the menu, not when they select something.
			menu.setOnDismissListener(null);
			events.itemSelected();
			if (item.getItemId() == R.id.image__choose_external__get
					|| item.getGroupId() == R.id.image__choose_external__get_group) {
				getContent.launch(item.getIntent());
				return true;
			} else if (item.getItemId() == R.id.image__choose_external__pick
					|| item.getGroupId() == R.id.image__choose_external__pick_group) {
				pickImage.launch(item.getIntent());
				return true;
			} else if (item.getItemId() == R.id.image__choose_external__capture
					|| item.getGroupId() == R.id.image__choose_external__capture_group) {
				captureImage.launch(item.getIntent());
				return true;
			} else {
				throw new IllegalArgumentException("Unknown menu item: " + item);
			}
			// Execution continues in registerForActivityResult callbacks.
		});
	}

	private static @NonNull PopupMenu createMenu(@NonNull Activity activity, @NonNull View anchor) {
		PopupMenu menu = new PopupMenu(activity, anchor);
		menu.setGravity(Gravity.TOP | Gravity.START);
		menu.setForceShowIcon(true); // See fixIcons().
		menu.inflate(R.menu.image__choose_external);
		return menu;
	}

	public void show() {
		// Set dismiss listener right before showing to make sure it exists.
		// It might be cleared when an item is selected. See #setOnMenuItemClickListener.
		menu.setOnDismissListener(menu -> events.onCancelled());
		resolveIntents(menu.getMenu());
		menu.show();
	}

	private void resolveIntents(@NonNull Menu menu) {
		populate(menu, R.id.image__choose_external__pick, R.id.image__choose_external__pick_group);
		populate(menu, R.id.image__choose_external__get, R.id.image__choose_external__get_group);
		populate(menu, R.id.image__choose_external__capture, R.id.image__choose_external__capture_group);
		fixIcons(menu);
		showCapture(menu, ImageRequest.canLaunchCameraIntent(context));
	}

	private void populate(@NonNull Menu menu, @IdRes int itemId, @IdRes int groupId) {
		MenuItem item = menu.findItem(itemId);
		menu.addIntentOptions(groupId, Menu.NONE, item.getOrder() + 1, null, null, item.getIntent(), 0, null);
	}

	private void showCapture(@NonNull Menu menu, boolean canCapture) {
		MenuItem captureItem = menu.findItem(R.id.image__choose_external__capture);
		ViewTools.visibleIf(captureItem, canCapture);
		menu.setGroupVisible(R.id.image__choose_external__capture_group, captureItem.isVisible());
	}

	private void fixIcons(@NonNull Menu menu) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setIcon(fix(item.getIcon(), item.getGroupId() != Menu.NONE, context.getResources()));
		}
	}

	/**
	 * Ensure all drawables have the same size, otherwise they're misaligned.
	 * Also add some indentation to resolved package listings, to give some hierarchy without use of submenus.
	 * <p>
	 * {@link androidx.appcompat.view.menu.StandardMenuPopup}
	 * inflates {@link androidx.appcompat.R.layout.abc_popup_menu_item_layout}
	 * using {@link androidx.appcompat.view.menu.MenuAdapter}.
	 * Items in the adapter will be {@link androidx.appcompat.view.menu.ListMenuItemView}
	 * and their icons are added in {@link androidx.appcompat.view.menu.ListMenuItemView#insertIconView}
	 * from {@link androidx.appcompat.R.layout.abc_list_menu_item_icon}.
	 * @noinspection JavadocReference
	 */
	private static @Nullable Drawable fix(@Nullable Drawable icon, boolean sub, @NonNull Resources resources) {
		if (icon == null) {
			return null;
		}
		int indent = sub ? resources.getDimensionPixelSize(R.dimen.image__choose_external__menu_icon_indent) : 0;
		int size = resources.getDimensionPixelSize(R.dimen.image__choose_external__menu_icon_size);
		return new DrawableWrapperCompat(icon) {
			@Override public int getIntrinsicWidth() {
				// Adding indent reserves extra width, because ImageView will take this size.
				return size + indent;
			}
			@Override public int getIntrinsicHeight() {
				return size;
			}
			@Override public void setBounds(int left, int top, int right, int bottom) {
				// Shift left by `indent`, so the icon gets back to it's `size` width, and the left side of it will be empty.
				super.setBounds(left + indent, top, right, bottom);
			}
		};
	}

	public interface Events {
		void onCancelled();
		void itemSelected();
		void onGetContent(@NonNull Uri result);
		void onPick(@NonNull Uri result);
		void onCapture(@NonNull Uri result);
	}

	private static class ExplicitAbleActivityResultLauncher<I> {
		private final @NonNull Context context;
		private final @NonNull ActivityResultContract<I, ?> contract;
		private final @NonNull I input;
		private final @NonNull ActivityResultLauncher<Intent> launcher;

		private <O, C extends Context & ActivityResultCaller> ExplicitAbleActivityResultLauncher(
				@NonNull C context,
				@NonNull ActivityResultContract<I, O> contract,
				@NonNull I input, @NonNull ActivityResultCallback<O> callback
		) {
			this.context = context;
			this.contract = contract;
			this.input = input;
			this.launcher = context.registerForActivityResult(
					new ActivityResultContracts.StartActivityForResult(),
					ar -> callback.onActivityResult(
							contract.parseResult(ar.getResultCode(), ar.getData())
					)
			);
		}

		/**
		 * Create a generic intent for the {@link #contract} and {@link #input}.
		 */
		public @NonNull Intent createIntent() {
			return contract.createIntent(context, input);
		}

		/**
		 * Launch the generic intent generated by {@link #createIntent()},
		 * or launch a package-specific explicit intent (overridden component) from the same.
		 */
		public void launch(@NonNull Intent input) {
			launcher.launch(input);
		}
	}
}
