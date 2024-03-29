package net.twisterrob.android.test.espresso.idle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideAccessor;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.EngineIdleWatcher;

import androidx.test.core.app.ApplicationProvider;

public class GlideIdlingResource extends AsyncIdlingResource {
	private static final Logger LOG = LoggerFactory.getLogger(GlideIdlingResource.class);

	private final Runnable callTransitionToIdle = this::transitionToIdle;
	private EngineIdleWatcher watcher;
	private Engine currentEngine;

	@Override public String getName() {
		return "Glide";
	}

	@Override protected boolean isIdle() {
		// Glide is a singleton, hence Engine should be too; just lazily initialize when needed.
		// In case Glide is replaced, this will still work.
		Glide glide = Glide.get(ApplicationProvider.getApplicationContext());
		Engine engine = GlideAccessor.getEngine(glide);
		if (currentEngine != engine) {
			if (watcher != null) {
				watcher.unsubscribe(callTransitionToIdle);
			}
			EngineIdleWatcher oldWatcher = watcher;
			try {
				watcher = new EngineIdleWatcher(engine);
				watcher.setLogEvents(isVerbose());
			} finally {
				if (currentEngine != null) {
					LOG.warn("Engine changed from {}({}) to {}({})", currentEngine, oldWatcher, engine, watcher);
				}
				currentEngine = engine;
			}
		}
		return isIdleCore();
	}

	private boolean isIdleCore() {
		return watcher.isIdle();
	}

	@Override protected void waitForIdleAsync() {
		watcher.subscribe(callTransitionToIdle);
	}

	@Override protected void transitionToIdle() {
		watcher.unsubscribe(callTransitionToIdle);
		super.transitionToIdle();
	}
}
