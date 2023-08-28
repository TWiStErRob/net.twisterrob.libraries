package net.twisterrob.android.view;

import java.util.Iterator;
import java.util.NoSuchElementException;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public class MenuIterable implements Iterable<MenuItem> {
	private final @NonNull Menu menu;

	public MenuIterable(@NonNull Menu menu) {
		this.menu = menu;
	}

	@Override public @NonNull Iterator<MenuItem> iterator() {
		return new Iterator<MenuItem>() {
			private int index = -1;
			private boolean currentItemRemoved = false;

			@Override public boolean hasNext() {
				return index + 1 < menu.size();
			}

			@Override public MenuItem next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				currentItemRemoved = false;
				return menu.getItem(++index);
			}

			@Override public void remove() {
				if (index == -1) {
					throw new IllegalStateException("Call next() first.");
				}
				if (currentItemRemoved) {
					throw new IllegalStateException("Item was already removed.");
				}
				// CONSIDER using internal MenuBuilder.removeItemAt(index: Int).
				MenuItem item = menu.getItem(index);
				if (item.getItemId() == Menu.NONE) {
					throw new IllegalStateException("Item has no ID, so cannot be removed.");
				}
				menu.removeItem(item.getItemId());
				currentItemRemoved = true;
			}
		};
	}
}
