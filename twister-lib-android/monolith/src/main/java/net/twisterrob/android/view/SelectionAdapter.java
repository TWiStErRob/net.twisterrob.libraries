package net.twisterrob.android.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import net.twisterrob.android.adapter.WrappingAdapter;

// CONSIDER override mDataObserver to adjust selectedItems and excludedItems indices when items move
// Changing a single item's category in the category's list keeps selection, but selects different items.
// based on http://www.grokkingandroid.com/statelistdrawables-for-recyclerview-selection/
public class SelectionAdapter<VH extends RecyclerView.ViewHolder> extends WrappingAdapter<VH> {
	private final Set<Long> selectedItems = new HashSet<>();
	private final Set<Long> excludedItems = new HashSet<>();

	public SelectionAdapter(Adapter<VH> wrapped) {
		super(wrapped);
	}

	@Override protected void setWrappedAdapter(@NonNull RecyclerView.Adapter<VH> wrapped) {
		super.setWrappedAdapter(wrapped);
		assert hasStableIds();
	}

	@Override public void onBindViewHolder(VH holder, int position) {
		boolean selected = isSelected(position);
		if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
			holder.itemView.setSelected(selected);
			holder.itemView.setActivated(selected);
		} else {
			holder.itemView.setSelected(selected);
		}
		super.onBindViewHolder(holder, position);
	}

	public void resetSelectable() {
		excludedItems.clear();
	}

	public void setSelectable(int position, boolean isSelectable) {
		if (!isSelectable) {
			excludedItems.add(getItemId(position));
		} else {
			excludedItems.remove(getItemId(position));
			setSelected(position, false);
		}
	}

	public boolean isSelectable(int position) {
		return !excludedItems.contains(getItemId(position));
	}

	public boolean isSelected(int position) {
		return selectedItems.contains(getItemId(position));
	}

	public void setSelected(int position, boolean isSelected) {
		if (!isSelectable(position)) {
			return;
		}
		if (isSelected) {
			selectedItems.add(getItemId(position));
		} else {
			selectedItems.remove(getItemId(position));
		}
	}

	public void toggleSelection(int position) {
		if (!isSelectable(position)) {
			return;
		}
		setSelected(position, !isSelected(position));
		notifyItemChanged(position);
	}

	public void selectRange(int positionStart, int itemCount) {
		selectedItems.clear();
		for (int position = positionStart; position < positionStart + itemCount; position++) {
			if (isSelectable(position)) {
				selectedItems.add(getItemId(position));
			}
		}
		notifyItemRangeChanged(positionStart, itemCount);
	}

	@SuppressLint("NotifyDataSetChanged") // The selection is likely non-contiguous, notify to refresh everything. 
	public void clearSelections() {
		selectedItems.clear();
		notifyDataSetChanged();
	}

	public int getSelectedItemCount() {
		return selectedItems.size();
	}

	public @NonNull long[] getSelectedIds() {
		long[] IDs = new long[selectedItems.size()];
		int i = 0;
		for (long id : selectedItems) {
			IDs[i++] = id;
		}
		return IDs;
	}

	@SuppressLint("NotifyDataSetChanged") // The selection is likely non-contiguous, notify to refresh everything. 
	public void setSelectedIds(@NonNull long[] selection) {
		selectedItems.clear();
		for (long id : selection) {
			selectedItems.add(id);
		}
		notifyDataSetChanged();
	}

	@SuppressLint("NotifyDataSetChanged") // The selection is likely non-contiguous, notify to refresh everything. 
	public void setSelectedItems(@NonNull Collection<Integer> positions) {
		selectedItems.clear();
		for (int position : positions) {
			if (isSelectable(position)) {
				selectedItems.add(getItemId(position));
			}
		}
		notifyDataSetChanged();
	}
}
