package net.twisterrob.android.utils.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class LoggingAnimationListener implements AnimationListener {
	private static final Logger LOG = LoggerFactory.getLogger("Animation");

	@Override public void onAnimationStart(Animation animation) {
		LOG.trace("onAnimationStart({})", animation);
	}
	@Override public void onAnimationEnd(Animation animation) {
		LOG.trace("onAnimationEnd({})", animation);
	}
	@Override public void onAnimationRepeat(Animation animation) {
		LOG.trace("onAnimationRepeat({})", animation);
	}
}
