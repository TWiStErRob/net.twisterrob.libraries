package net.twisterrob.android.utils.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.View;

import net.twisterrob.java.annotations.DebugHelper;

@DebugHelper
public class LoggingOnClickListener implements View.OnClickListener {
	private static final Logger LOG = LoggerFactory.getLogger("ViewOnClick");

	@Override public void onClick(View v) {
		LOG.trace("onClick({})", v);
	}
}
