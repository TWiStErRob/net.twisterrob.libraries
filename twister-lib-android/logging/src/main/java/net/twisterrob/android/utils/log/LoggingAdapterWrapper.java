package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.DataSetObserver;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.twisterrob.android.utils.tools.StringerTools;

public class LoggingAdapterWrapper implements Adapter {
	private static final Logger LOG = LoggerFactory.getLogger("Adapter");

	private final @NonNull Adapter wrapped;

	public LoggingAdapterWrapper(@NonNull Adapter wrapped) {
		this.wrapped = wrapped;
	}

	public @NonNull Adapter getWrapped() {
		return wrapped;
	}

	@Override public void registerDataSetObserver(DataSetObserver observer) {
		log("registerDataSetObserver", observer);
		wrapped.registerDataSetObserver(observer);
	}

	@Override public void unregisterDataSetObserver(DataSetObserver observer) {
		log("unregisterDataSetObserver", observer);
		wrapped.unregisterDataSetObserver(observer);
	}

	@Override public int getCount() {
		log("getCount");
		int ret = wrapped.getCount();
		logReturn("getCount", ret);
		return ret;
	}

	@Override public Object getItem(int position) {
		log("getItem", position);
		Object ret = wrapped.getItem(position);
		logReturn("getItem", ret, position);
		return ret;
	}

	@Override public long getItemId(int position) {
		log("getItemId", position);
		long ret = wrapped.getItemId(position);
		logReturn("getItemId", ret, position);
		return ret;
	}

	@Override public boolean hasStableIds() {
		log("hasStableIds");
		boolean ret = wrapped.hasStableIds();
		logReturn("hasStableIds", ret);
		return ret;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		log("getView", position, convertView, parent);
		View ret = wrapped.getView(position, convertView, parent);
		logReturn("getView", ret, position, convertView, parent);
		return ret;
	}

	@Override public int getItemViewType(int position) {
		log("getItemViewType", position);
		int ret = wrapped.getItemViewType(position);
		logReturn("getItemViewType", ret, position);
		return ret;
	}

	@Override public int getViewTypeCount() {
		log("getViewTypeCount");
		int ret = wrapped.getViewTypeCount();
		logReturn("getViewTypeCount", ret);
		return ret;
	}

	@Override public boolean isEmpty() {
		log("isEmpty");
		boolean ret = wrapped.isEmpty();
		logReturn("isEmpty", ret);
		return ret;
	}

	@RequiresApi(Build.VERSION_CODES.O)
	@Override public @Nullable CharSequence[] getAutofillOptions() {
		log("getAutofillOptions");
		CharSequence[] ret = wrapped.getAutofillOptions();
		logReturn("getAutofillOptions", ret);
		return ret;
	}

	protected void log(@NonNull String name, @NonNull Object... args) {
		LoggingDebugProvider.LoggingHelper.log(getLog(), getName(), name, null, args);
	}

	protected void logReturn(@NonNull String name, Object ret, @NonNull Object... args) {
		LoggingDebugProvider retInfo = () -> "returned " + StringerTools.toString(ret);
		LoggingDebugProvider.LoggingHelper.log(getLog(), getName(), name, retInfo, args);
	}

	protected @NonNull Logger getLog() {
		return LOG;
	}

	protected @NonNull String getName() {
		return StringerTools.toNameString(getWrapped());
	}
}
