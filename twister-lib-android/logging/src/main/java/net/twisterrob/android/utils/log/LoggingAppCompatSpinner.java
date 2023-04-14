package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import net.twisterrob.android.utils.tools.StringerTools;

public class LoggingAppCompatSpinner extends AppCompatSpinner {

	private static final Logger LOG = LoggerFactory.getLogger("AppCompatSpinner");

	protected @Nullable LoggingDebugProvider debugInfoProvider;

	public LoggingAppCompatSpinner(@NonNull Context context) {
		super(logCtor(context, context));
		log("ctor", context);
	}

	public LoggingAppCompatSpinner(@NonNull Context context, int mode) {
		super(logCtor(context, context, mode),
				mode);
		log("ctor", context, mode);
	}

	public LoggingAppCompatSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(logCtor(context, context, attrs),
				attrs);
		log("ctor", context, attrs);
	}

	public LoggingAppCompatSpinner(@NonNull Context context, @Nullable AttributeSet attrs,
			int defStyleAttr) {
		super(logCtor(context, context, attrs, defStyleAttr),
				attrs, defStyleAttr);
		log("ctor", context, attrs, defStyleAttr);
	}

	public LoggingAppCompatSpinner(@NonNull Context context, @Nullable AttributeSet attrs,
			int defStyleAttr, int mode) {
		super(logCtor(context, context, attrs, defStyleAttr, mode),
				attrs, defStyleAttr, mode);
		log("ctor", context, attrs, defStyleAttr, mode);
	}

	public LoggingAppCompatSpinner(@NonNull Context context, @Nullable AttributeSet attrs,
			int defStyleAttr, int mode, Resources.Theme popupTheme) {
		super(logCtor(context, context, attrs, defStyleAttr, mode, popupTheme),
				attrs, defStyleAttr, mode, popupTheme);
		log("ctor", context, attrs, defStyleAttr, mode, popupTheme);
	}

	@Override public void setSelection(int position) {
		log("setSelection", position);
		super.setSelection(position);
	}

	@Override public void setSelection(int position, boolean animate) {
		log("setSelection", position, animate);
		super.setSelection(position, animate);
	}

	@Override public void setAdapter(SpinnerAdapter adapter) {
		log("setAdapter", adapter);
		super.setAdapter(new LoggingSpinnerAdapterWrapper(adapter));
	}

	@Override public SpinnerAdapter getAdapter() {
		SpinnerAdapter adapter = super.getAdapter();
		if (adapter instanceof LoggingSpinnerAdapterWrapper) {
			return ((LoggingSpinnerAdapterWrapper)adapter).getWrapped();
		} else {
			return adapter;
		}
	}

	/**
	 * Note: this will break {@link #getOnItemClickListener()},
	 * but it's final, so can't override to unwrap.
	 */
	@Override public void setOnItemClickListener(OnItemClickListener listener) {
		log("setOnItemClickListener", listener);
		if (listener == null) {
			super.setOnItemClickListener(null);
		} else {
			super.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(
						AdapterView<?> parent, View view, int position, long id
				) {
					log("onItemClick", parent, view, position, id);
					listener.onItemClick(parent, view, position, id);
				}
			});
		}
	}

	/**
	 * Note: this will break {@link #getOnItemSelectedListener()},
	 * but it's final, so can't override to unwrap.
	 */
	@Override public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
		log("setOnItemSelectedListener", listener);
		if (listener == null) {
			super.setOnItemSelectedListener(null);
		} else {
			super.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override public void onItemSelected(
						AdapterView<?> parent, View view, int position, long id
				) {
					log("onItemSelected", parent, view, position, id);
					listener.onItemSelected(parent, view, position, id);
				}
				@Override public void onNothingSelected(AdapterView<?> parent) {
					log("onNothingSelected", parent);
					listener.onNothingSelected(parent);
				}
			});
		}
	}

	/**
	 * Note: this will break {@link #getOnItemLongClickListener()},
	 * but it's final, so can't override to unwrap.
	 */
	@Override public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		log("setOnItemLongClickListener", listener);
		if (listener == null) {
			super.setOnItemLongClickListener(null);
		} else {
			super.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override public boolean onItemLongClick(
						AdapterView<?> parent, View view, int position, long id
				) {
					log("onItemLongClick", parent, view, position, id);
					boolean ret = listener.onItemLongClick(parent, view, position, id);
					log("onItemLongClick", parent, view, position, id, ret);
					return ret;
				}
			});
		}
	}

	private static <T> T logCtor(T ret, @NonNull Object... args) {
		LoggingDebugProvider.LoggingHelper.log(LOG, "new", "<init>", null, args);
		return ret;
	}

	protected void log(@NonNull String name, @NonNull Object... args) {
		LoggingDebugProvider.LoggingHelper.log(LOG, getName(), name, debugInfoProvider, args);
	}

	protected @NonNull String getName() {
		return StringerTools.toNameString(this);
	}
}
