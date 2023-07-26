package net.twisterrob.android.test.espresso.idle;

import org.junit.rules.ExternalResource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

public class GlideResetRule extends ExternalResource {
	private final @NonNull Context appContext;

	public GlideResetRule() {
		this(ApplicationProvider.getApplicationContext());
	}

	public GlideResetRule(@NonNull Context appContext) {
		this.appContext = appContext;
	}

	@Override protected void before() {
		GlideResetter.resetGlide(appContext);
	}

	@Override protected void after() {
		GlideResetter.resetGlide(appContext);
	}
}
