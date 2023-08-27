package net.twisterrob.android.content;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat;
import androidx.appcompat.widget.PopupMenu;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.utils.tools.ViewTools;

public class ExternalImageMenu {
	private static final short REQUEST_CODE_BASE = 0x4100;
	private static final short REQUEST_CODE_PICK = REQUEST_CODE_BASE | (1 << 1);
	private static final short REQUEST_CODE_GET = REQUEST_CODE_BASE | (1 << 2);
	private static final short REQUEST_CODE_CAPTURE = REQUEST_CODE_BASE | (1 << 3);

	private final @NonNull PopupMenu menu;
	private final @NonNull ComponentActivity activity;

	public ExternalImageMenu(
			@NonNull ComponentActivity activity,
			@NonNull View anchor,
			@NonNull ImageRequest request,
			@NonNull Listeners listeners
	) {
		this.activity = activity;

		menu = new PopupMenu(activity, anchor);
		menu.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		menu.setForceShowIcon(true);
		menu.inflate(net.twisterrob.android.capture_image.R.menu.image__choose_external);
		menu.getMenu().findItem(R.id.image__choose_external__capture).setIntent(request.createCaptureImage());
		menu.getMenu().findItem(R.id.image__choose_external__get).setIntent(request.createGetContent());
		menu.getMenu().findItem(R.id.image__choose_external__pick).setIntent(request.createPick());
		menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override public void onDismiss(PopupMenu menu) {
				listeners.onCancelled();
			}
		});
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressWarnings("deprecation") // STOPSHIP contracts
			@Override public boolean onMenuItemClick(MenuItem item) {
				listeners.itemSelected();
				if (item.getItemId() == R.id.image__choose_external__get
						|| item.getGroupId() == R.id.image__choose_external__get_group) {
					activity.startActivityForResult(item.getIntent(), REQUEST_CODE_GET);
					return true;
				} else if (item.getItemId() == R.id.image__choose_external__pick
						|| item.getGroupId() == R.id.image__choose_external__pick_group) {
					activity.startActivityForResult(item.getIntent(), REQUEST_CODE_PICK);
					return true;
				} else if (item.getItemId() == R.id.image__choose_external__capture
						|| item.getGroupId() == R.id.image__choose_external__capture_group) {
					activity.startActivityForResult(item.getIntent(), REQUEST_CODE_CAPTURE);
					return true;
				} else {
					throw new IllegalArgumentException("Unknown menu item: " + item);
				}
				// Execution continues in onActivityResult.
			}
		});
	}

	public void show() {
		resolveIntents(menu.getMenu());
	}

	private void resolveIntents(@NonNull Menu menu) {
		populate(menu, R.id.image__choose_external__pick_group, R.id.image__choose_external__pick, 2);
		populate(menu, R.id.image__choose_external__get_group, R.id.image__choose_external__get, 4);
		populate(menu, R.id.image__choose_external__capture_group, R.id.image__choose_external__capture, 6);
		fixIcons(menu);
		showCapture(menu, ImageRequest.canLaunchCameraIntent(activity));
	}

	private void populate(@NonNull Menu menu, @IdRes int groupId, @IdRes int itemRes, int order) {
		Intent intent = menu.findItem(itemRes).getIntent();
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

	public @Nullable Uri onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		Uri pic;

		pic = ImageRequest.getPictureUriFromResult(REQUEST_CODE_PICK, requestCode, resultCode, data);
		if (pic != null) {
			return pic;
		}
		pic = ImageRequest.getPictureUriFromResult(REQUEST_CODE_GET, requestCode, resultCode, data);
		if (pic != null) {
			return pic;
		}
		pic = ImageRequest.getPictureUriFromResult(REQUEST_CODE_CAPTURE, requestCode, resultCode,
				data);
		if (pic != null) {
			return pic;
		}
		return null;
	}

	public interface Listeners {
		void onCancelled();
		void itemSelected();
	}
}
