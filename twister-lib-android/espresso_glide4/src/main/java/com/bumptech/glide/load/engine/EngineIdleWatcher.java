package com.bumptech.glide.load.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;

/**
 * This class is the bridge between package private stuff and the world, don't try to inline it.
 */
public class EngineIdleWatcher implements EngineExternalLifecycle.PhaseCallbacks {
	private static final Logger LOG = LoggerFactory.getLogger("EngineIdleWatcher");
	private final Set<Runnable> idleCallbacks = new HashSet<>();
	private final EngineExternalLifecycle lifecycle;
	private final boolean logEvents;

	public EngineIdleWatcher(@NonNull Engine engine, boolean logEvents) {
		this.logEvents = logEvents;
		this.lifecycle = new EngineExternalLifecycle(engine, this);
	}

	public void subscribe(Runnable callback) {
		idleCallbacks.add(callback);
	}

	public void unsubscribe(Runnable callback) {
		idleCallbacks.remove(callback);
	}

	public boolean isIdle() {
		Collection<?> jobs = lifecycle.getJobs();
		Collection<?> active = lifecycle.getActive();
		if (logEvents) {
			LOG.trace("{}/{}: active={}, jobs={}", this, lifecycle, active.size(), jobs.size());
		}
		return jobs.isEmpty() && active.isEmpty();
	}

	private void tryToCallBack() {
		if (isIdle()) {
			for (Runnable callback : idleCallbacks) {
				callback.run();
			}
		}
	}

	@Override public void starting(Engine engine, EngineKey key, EngineJob<?> job) {
		if (logEvents) {
			LOG.trace("{}.starting {}: {}", this, job, id(key));
		}
	}
	@Override public void finishing(Engine engine, EngineKey key, EngineJob<?> job) {
		if (logEvents) {
			LOG.trace("{}.finishing {}: {}", this, job, id(key));
		}
	}
	@Override public void cancelled(Engine engine, EngineKey key, EngineJob<?> job) {
		if (logEvents) {
			LOG.trace("{}.cancelled {}: {}", this, job, id(key));
		}
		tryToCallBack();
	}
	@Override public void loadSuccess(Engine engine, EngineKey key, EngineJob<?> job) {
		if (logEvents) {
			LOG.trace("{}.loadSuccess {}: {}", this, job, id(key));
		}
		tryToCallBack();
	}
	@Override public void loadFailure(Engine engine, EngineKey key, EngineJob<?> job) {
		if (logEvents) {
			LOG.trace("{}.loadFailure {}: {}", this, job, id(key));
		}
		tryToCallBack();
	}

	private static @NonNull String id(@NonNull EngineKey key) {
		return EngineKeyAccessor.toStringHack(key);
	}
}
