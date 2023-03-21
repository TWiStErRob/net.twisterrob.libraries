package net.twisterrob.android.test.junit.rules;

import org.junit.rules.ExternalResource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import net.twisterrob.android.test.SystemAnimations;

public class SystemAnimationsRule extends ExternalResource {
	private final @NonNull SystemAnimations systemAnimations;

	public SystemAnimationsRule() {
		this(InstrumentationRegistry.getInstrumentation().getContext());
	}

	public SystemAnimationsRule(@NonNull Context context) {
		this(new SystemAnimations(context));
	}

	public SystemAnimationsRule(@NonNull SystemAnimations systemAnimations) {
		this.systemAnimations = systemAnimations;
	}

	@Override protected void before() {
		systemAnimations.backup();
		systemAnimations.disableAll();
	}

	@Override protected void after() {
		systemAnimations.restore();
	}
}
