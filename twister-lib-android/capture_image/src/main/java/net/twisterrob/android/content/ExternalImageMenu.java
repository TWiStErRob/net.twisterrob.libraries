package net.twisterrob.android.content;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.PickVisualMediaRequestKt;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat;
import androidx.appcompat.widget.PopupMenu;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.utils.tools.ViewTools;

public class ExternalImageMenu {
	private static final String IMAGE_MIME_TYPE = "image/*";
	private static final PickVisualMediaRequest IMAGE_ONLY = // PickVisualMediaRequest(ImageOnly)
			PickVisualMediaRequestKt.PickVisualMediaRequest(
					ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE
			);

	private final @NonNull ComponentActivity activity;
	private final @NonNull Uri target;

	private final @NonNull PopupMenu menu;
	private final @NonNull ActivityResultLauncher<Uri> captureImage;
	private final @NonNull ActivityResultLauncher<String> getContent;
	private final @NonNull ActivityResultLauncher<PickVisualMediaRequest> pickImage;
	private final @NonNull ActivityResultLauncher<Intent> getContentSpecific;

	public ExternalImageMenu(
			@NonNull ComponentActivity activity,
			@NonNull View anchor,
			@NonNull Uri target,
			@NonNull Listeners listeners
	) {
		this.activity = activity;
		this.target = target;
		this.pickImage = activity.registerForActivityResult(
				new ActivityResultContracts.PickVisualMedia(), // STOPSHIP real pick?
				(@Nullable Uri result) -> {
					if (result != null) {
						listeners.onPick(result);
					} else {
						listeners.onCancelled();
					}
				}
		);
		this.getContent = activity.registerForActivityResult(
				new ActivityResultContracts.GetContent(),
				(@Nullable Uri result) -> {
					if (result != null) {
						listeners.onGetContent(result);
					} else {
						listeners.onCancelled();
					}
				}
		);
		this.captureImage = activity.registerForActivityResult(
				new ActivityResultContracts.TakePicture(),
				(@Nullable Boolean result) -> {
					if (Boolean.TRUE.equals(result)) {
						listeners.onCapture(target);
					} else {
						listeners.onCancelled();
					}
				}
		);
		this.getContentSpecific = activity.registerForActivityResult( // STOPSHIP generalize
				new ActivityResultContracts.StartActivityForResult(),
				(@NonNull ActivityResult ar) ->
				{
					Uri result = (Uri)getContent.getContract().parseResult(ar.getResultCode(), ar.getData());
					if (result != null) {
						listeners.onGetContent(result);
					} else {
						listeners.onCancelled();
					}
				}
		);
		menu = createMenu(activity, anchor);
		menu.setOnDismissListener(menu -> listeners.onCancelled());
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override public boolean onMenuItemClick(MenuItem item) {
				menu.setOnDismissListener(null);
				listeners.itemSelected();
				if (item.getItemId() == R.id.image__choose_external__get) {
					getContent.launch(IMAGE_MIME_TYPE);
					return true;
				} else if (item.getGroupId() == R.id.image__choose_external__get_group) {
					getContentSpecific.launch(item.getIntent());
					return true;
				} else if (item.getItemId() == R.id.image__choose_external__pick) {
					pickImage.launch(IMAGE_ONLY);
					return true;
				} else if (item.getGroupId() == R.id.image__choose_external__pick_group) {
					pickImage.launch(IMAGE_ONLY); // STOPSHIP
					return true;
				} else if (item.getItemId() == R.id.image__choose_external__capture) {
					captureImage.launch(target);
					return true;
				} else if (item.getGroupId() == R.id.image__choose_external__capture_group) {
					captureImage.launch(target); // STOPSHIP
					return true;
				} else {
					throw new IllegalArgumentException("Unknown menu item: " + item);
				}
				// Execution continues in registerForActivityResult callback.
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
		populate(menu, R.id.image__choose_external__pick_group, pickImage.getContract().createIntent(activity, IMAGE_ONLY), 2);
		populate(menu, R.id.image__choose_external__get_group, getContent.getContract().createIntent(activity, IMAGE_MIME_TYPE), 4);
		populate(menu, R.id.image__choose_external__capture_group, captureImage.getContract().createIntent(activity, target), 6);
		fixIcons(menu);
		showCapture(menu, ImageRequest.canLaunchCameraIntent(activity));
	}

	private void populate(@NonNull Menu menu, @IdRes int groupId, Intent intent, int order) {
		menu.addIntentOptions(groupId, Menu.NONE, order, activity.getComponentName(), null, intent, 0, null);
	}

	private void showCapture(@NonNull Menu menu, boolean canCapture) {
		MenuItem captureItem = menu.findItem(R.id.image__choose_external__capture);
		ViewTools.visibleIf(captureItem, canCapture);
		menu.setGroupVisible(R.id.image__choose_external__capture_group, captureItem.isVisible());
	}

	private void fixIcons(@NonNull Menu menu) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setIcon(fix(item.getIcon(), item.getGroupId() != Menu.NONE, activity.getResources()));
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
		void onGetContent(Uri result);
		void onPick(Uri result);
		void onCapture(Uri result);
	}
}
