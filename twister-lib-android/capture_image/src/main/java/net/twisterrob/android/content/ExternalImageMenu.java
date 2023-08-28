package net.twisterrob.android.content;

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
import net.twisterrob.android.utils.tools.ViewTools;

public class ExternalImageMenu {

	private final @NonNull Context context;
	private final @NonNull PopupMenu menu;

	public ExternalImageMenu(
			@NonNull ComponentActivity activity,
			@NonNull View anchor,
			@NonNull Uri target,
			@NonNull Listeners listeners
	) {
		this.context = activity;
		ExplicitAbleActivityResultLauncher<PickVisualMediaRequest> pickImage = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.PickVisualMedia(), // STOPSHIP real pick?
				PickVisualMediaRequestKt.PickVisualMediaRequest(
						ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE
				),
				(@Nullable Uri result) -> {
					if (result != null) {
						listeners.onPick(result);
					} else {
						listeners.onCancelled();
					}
				}
		);
		ExplicitAbleActivityResultLauncher<String> getContent = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.GetContent(),
				"image/*",
				(@Nullable Uri result) -> {
					if (result != null) {
						listeners.onGetContent(result);
					} else {
						listeners.onCancelled();
					}
				}
		);
		ExplicitAbleActivityResultLauncher<Uri> captureImage = new ExplicitAbleActivityResultLauncher<>(
				activity,
				new ActivityResultContracts.TakePicture(),
				target,
				(@Nullable Boolean result) -> {
					if (Boolean.TRUE.equals(result)) {
						listeners.onCapture(target);
					} else {
						listeners.onCancelled();
					}
				}
		);
		menu = createMenu(activity, anchor);
		menu.getMenu().findItem(R.id.image__choose_external__get).setIntent(getContent.createIntent());
		menu.getMenu().findItem(R.id.image__choose_external__pick).setIntent(pickImage.createIntent());
		menu.getMenu().findItem(R.id.image__choose_external__capture).setIntent(captureImage.createIntent());
		menu.setOnDismissListener(menu -> listeners.onCancelled());
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override public boolean onMenuItemClick(@NonNull MenuItem item) {
				menu.setOnDismissListener(null);
				listeners.itemSelected();
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
			}
		});
	}

	private static @NonNull PopupMenu createMenu(@NonNull Activity activity, @NonNull View anchor) {
		@NonNull PopupMenu menu = new PopupMenu(activity, anchor);
		menu.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		menu.setForceShowIcon(true);
		menu.inflate(R.menu.image__choose_external);
		return menu;
	}

	public void show() {
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

	private static @Nullable Drawable fix(@Nullable Drawable icon, boolean sub, @NonNull Resources resources) {
		if (icon == null) {
			return null;
		}
		int indent = sub ? resources.getDimensionPixelSize(R.dimen.image__choose_external__menu_icon_indent) : 0;
		int size = resources.getDimensionPixelSize(R.dimen.image__choose_external__menu_icon_size);
		return new DrawableWrapperCompat(icon) {
			@Override public int getIntrinsicWidth() {
				// Extra indent reserves size.
				return size + indent;
			}
			@Override public int getIntrinsicHeight() {
				return size;
			}
			@Override public void setBounds(int left, int top, int right, int bottom) {
				// Only shift left by `indent`, so the icon gets back to it's `size`.
				super.setBounds(left + indent, top, right, bottom);
			}
		};
	}

	public interface Listeners {
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
