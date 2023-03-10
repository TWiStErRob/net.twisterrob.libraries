package net.twisterrob.android.utils.listeners;

import org.slf4j.*;

import android.annotation.SuppressLint;
import android.view.*;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("ClickableViewAccessibility")
@DebugHelper
public class LoggingOnTouchListener implements View.OnTouchListener {
	private static final Logger LOG = LoggerFactory.getLogger("ViewOnTouch");

	@Override public boolean onTouch(View v, MotionEvent event) {
		LOG.trace("onTouch({}, {})", v, event);
		return false;
	}
}
