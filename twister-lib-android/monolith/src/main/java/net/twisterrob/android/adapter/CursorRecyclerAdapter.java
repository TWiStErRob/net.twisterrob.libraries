package net.twisterrob.android.adapter;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Provide a {@link RecyclerView.Adapter} implementation with cursor support.
 *
 * Child classes only need to implement {@link #onCreateViewHolder(android.view.ViewGroup, int)} and
 * {@link #onBindViewHolder(RecyclerView.ViewHolder, android.database.Cursor)}.
 *
 * This class does not implement deprecated fields and methods from {@link android.widget.CursorAdapter CursorAdapter}!
 * Incidentally, only {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER FLAG_REGISTER_CONTENT_OBSERVER}
 * is available, so the flag is implied, and only the Adapter behavior using this flag has been ported.
 *
 * @param <VH> {@inheritDoc}
 *
 * @see <a href="https://gist.github.com/Shywim/127f207e7248fe48400b">Github > Shywim > CursorRecyclerAdapter.java</a>
 */
public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH>
		implements Filterable, CursorFilter.CursorFilterClient {
	public static final int AUTO_REQUERY_BG = androidx.cursoradapter.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;

	private boolean mDataValid;
	private int mRowIDColumn;
	private Cursor mCursor;
	private ChangeObserver mChangeObserver;
	private DataSetObserver mDataSetObserver;
	private CursorFilter mCursorFilter;
	private FilterQueryProvider mFilterQueryProvider;

	public CursorRecyclerAdapter(@Nullable Cursor cursor) {
		this(cursor, 0);
	}
	public CursorRecyclerAdapter(@Nullable Cursor cursor, int flags) {
		setHasStableIds(true);
		init(cursor, flags);
	}

	void init(@Nullable Cursor c, int flags) {
		boolean cursorPresent = c != null;
		mCursor = c;
		mDataValid = cursorPresent;
		mRowIDColumn = cursorPresent? c.getColumnIndexOrThrow("_id") : -1;

		if ((flags & AUTO_REQUERY_BG) == AUTO_REQUERY_BG) {
			mChangeObserver = new ChangeObserver();
			mDataSetObserver = new MyDataSetObserver();
		} else {
			mChangeObserver = null;
			mDataSetObserver = null;
		}

		if (cursorPresent) {
			if (mChangeObserver != null) {
				c.registerContentObserver(mChangeObserver);
			}
			if (mDataSetObserver != null) {
				c.registerDataSetObserver(mDataSetObserver);
			}
		}
	}

	/**
	 * This method will move the Cursor to the correct position and call
	 * {@link #onBindViewHolder(RecyclerView.ViewHolder,
	 * android.database.Cursor)}.
	 *
	 * @param holder {@inheritDoc}
	 * @param i {@inheritDoc}
	 */
	@SuppressWarnings("NullableProblems")
	@Override public void onBindViewHolder(@NonNull VH holder, int i) {
		if (!mDataValid) {
			throw new IllegalStateException("this should only be called when the cursor is valid");
		}
		if (!mCursor.moveToPosition(i)) {
			throw new IllegalStateException("couldn't move cursor to position " + i);
		}
		onBindViewHolder(holder, mCursor);
	}

	/**
	 * See {@link android.widget.CursorAdapter#bindView(android.view.View, android.content.Context,
	 * android.database.Cursor)},
	 * {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
	 *
	 * @param holder View holder.
	 * @param cursor The cursor from which to get the data. The cursor is already
	 * moved to the correct position.
	 */
	public abstract void onBindViewHolder(@NonNull VH holder, @NonNull Cursor cursor);

	@Override public int getItemCount() {
		if (mDataValid && mCursor != null) {
			return mCursor.getCount();
		} else {
			return 0;
		}
	}

	/**
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	@Override public long getItemId(int position) {
		if (mDataValid && mCursor != null) {
			if (mCursor.moveToPosition(position)) {
				return mCursor.getLong(mRowIDColumn);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	// it's better to make this nullable , because most of the time the cursor is available when this is called
	@SuppressWarnings("ConstantConditions")
	public /*@Nullable*/ Cursor getCursor() {
		return mCursor;
	}

	/**
	 * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
	 * closed.
	 *
	 * @param cursor The new cursor to be used
	 */
	public void changeCursor(@Nullable Cursor cursor) {
		Cursor old = swapCursor(cursor);
		if (old != null) {
			old.close();
		}
	}

	/**
	 * Swap in a new Cursor, returning the old Cursor.  Unlike
	 * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
	 * closed.
	 *
	 * @param newCursor The new cursor to be used.
	 * @return Returns the previously set Cursor, or null if there wasa not one.
	 * If the given new Cursor is the same instance is the previously set
	 * Cursor, null is also returned.
	 */
	@SuppressLint("NotifyDataSetChanged") // Everything is changed, so notify all.
	@CheckResult
	public @Nullable Cursor swapCursor(@Nullable Cursor newCursor) {
		if (newCursor == mCursor) {
			return null;
		}
		Cursor oldCursor = mCursor;
		if (oldCursor != null) {
			if (mChangeObserver != null) {
				oldCursor.unregisterContentObserver(mChangeObserver);
			}
			if (mDataSetObserver != null) {
				oldCursor.unregisterDataSetObserver(mDataSetObserver);
			}
		}
		if (newCursor != null) {
			if (mChangeObserver != null) {
				newCursor.registerContentObserver(mChangeObserver);
			}
			if (mDataSetObserver != null) {
				newCursor.registerDataSetObserver(mDataSetObserver);
			}
			mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
			mDataValid = true;
			mCursor = newCursor;
			// notify the observers about the new cursor
			notifyDataSetChanged();
		} else {
			// Make sure getting count is before all mutation, otherwise it's invalid.
			int oldCount = getItemCount();
			mRowIDColumn = -1;
			mDataValid = false;
			mCursor = null;
			// notify the observers about the lack of a data set
			notifyItemRangeRemoved(0, oldCount); // =~= notifyDataSetInvalidated();
		}
		return oldCursor;
	}

	/**
	 * <p>Converts the cursor into a CharSequence. Subclasses should override this
	 * method to convert their results. The default implementation returns an
	 * empty String for null values or the default String representation of
	 * the value.</p>
	 *
	 * @param cursor the cursor to convert to a CharSequence
	 * @return a CharSequence representing the value
	 */
	public @NonNull CharSequence convertToString(@Nullable Cursor cursor) {
		return cursor == null? "" : cursor.toString();
	}

	/**
	 * Runs a query with the specified constraint. This query is requested
	 * by the filter attached to this adapter.
	 *
	 * The query is provided by a
	 * {@link android.widget.FilterQueryProvider}.
	 * If no provider is specified, the current cursor is not filtered and returned.
	 *
	 * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
	 * and the previous cursor is closed.
	 *
	 * This method is always executed on a background thread, not on the
	 * application's main thread (or UI thread.)
	 *
	 * Contract: when constraint is null or empty, the original results,
	 * prior to any filtering, must be returned.
	 *
	 * @param constraint the constraint with which the query must be filtered
	 *
	 * @return a Cursor representing the results of the new query
	 *
	 * @see #getFilter()
	 * @see #getFilterQueryProvider()
	 * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
	 */
	public @Nullable Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (mFilterQueryProvider != null) {
			return mFilterQueryProvider.runQuery(constraint);
		}

		return mCursor;
	}

	public Filter getFilter() {
		if (mCursorFilter == null) {
			mCursorFilter = new CursorFilter(this);
		}
		return mCursorFilter;
	}

	/**
	 * Returns the query filter provider used for filtering. When the
	 * provider is null, no filtering occurs.
	 *
	 * @return the current filter query provider or null if it does not exist
	 *
	 * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
	 * @see #runQueryOnBackgroundThread(CharSequence)
	 */
	public FilterQueryProvider getFilterQueryProvider() {
		return mFilterQueryProvider;
	}

	/**
	 * Sets the query filter provider used to filter the current Cursor.
	 * The provider's
	 * {@link android.widget.FilterQueryProvider#runQuery(CharSequence)}
	 * method is invoked when filtering is requested by a client of
	 * this adapter.
	 *
	 * @param filterQueryProvider the filter query provider or null to remove it
	 *
	 * @see #getFilterQueryProvider()
	 * @see #runQueryOnBackgroundThread(CharSequence)
	 */
	public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
		mFilterQueryProvider = filterQueryProvider;
	}

	/**
	 * Called when the {@link ContentObserver} on the cursor receives a change notification.
	 * Can be implemented by sub-class.
	 *
	 * @see ContentObserver#onChange(boolean)
	 */
	protected void onContentChanged() {
		// optional override
	}

	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler(Looper.getMainLooper()));
		}

		@Override public boolean deliverSelfNotifications() {
			return true;
		}

		@Override public void onChange(boolean selfChange) {
			onContentChanged();
		}
	}

	private class MyDataSetObserver extends DataSetObserver {
		@SuppressLint("NotifyDataSetChanged") // We don't know why it's changed, so just notify all.
		@Override public void onChanged() {
			mDataValid = true;
			notifyDataSetChanged();
		}

		@Override public void onInvalidated() {
			mDataValid = false;
			// notifyDataSetInvalidated();
			notifyItemRangeRemoved(0, getItemCount());
		}
	}
}

/**
 * The CursorFilter delegates most of the work to the CursorAdapter.
 * Subclasses should override these delegate methods to run the queries and convert the results into String
 * that can be used by auto-completion widgets.
 */
class CursorFilter extends Filter {
	private final CursorFilterClient mClient;

	interface CursorFilterClient {
		CharSequence convertToString(Cursor cursor);
		Cursor runQueryOnBackgroundThread(CharSequence constraint);
		Cursor getCursor();
		void changeCursor(Cursor cursor);
	}

	CursorFilter(CursorFilterClient client) {
		mClient = client;
	}

	@Override public CharSequence convertResultToString(Object resultValue) {
		return mClient.convertToString((Cursor)resultValue);
	}

	@Override protected FilterResults performFiltering(CharSequence constraint) {
		Cursor cursor = mClient.runQueryOnBackgroundThread(constraint);

		FilterResults results = new FilterResults();
		if (cursor != null) {
			results.count = cursor.getCount();
			results.values = cursor;
		} else {
			results.count = 0;
			results.values = null;
		}
		return results;
	}

	@Override protected void publishResults(CharSequence constraint, FilterResults results) {
		Cursor oldCursor = mClient.getCursor();

		if (results.values != null && results.values != oldCursor) {
			mClient.changeCursor((Cursor)results.values);
		}
	}
}
