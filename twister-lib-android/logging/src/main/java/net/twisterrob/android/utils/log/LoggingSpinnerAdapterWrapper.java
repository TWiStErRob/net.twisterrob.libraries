package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;

public class LoggingSpinnerAdapterWrapper extends LoggingAdapterWrapper implements SpinnerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger("SpinnerAdapter");

	private final @NonNull SpinnerAdapter wrapped;

	public LoggingSpinnerAdapterWrapper(@NonNull SpinnerAdapter wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	@Override public @NonNull SpinnerAdapter getWrapped() {
		return wrapped;
	}

	@Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
		log("getDropDownView", position, convertView, parent);
		View ret = wrapped.getDropDownView(position, convertView, parent);
		logReturn("getDropDownView", ret, position, convertView, parent);
		return ret;
	}

	@Override protected @NonNull Logger getLog() {
		return LOG;
	}
}
