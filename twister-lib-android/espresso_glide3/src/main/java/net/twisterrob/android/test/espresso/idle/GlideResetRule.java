package net.twisterrob.android.test.espresso.idle;

import org.junit.rules.ExternalResource;

import androidx.test.platform.app.InstrumentationRegistry;

public class GlideResetRule extends ExternalResource {
	@Override protected void before() {
		GlideResetter.resetGlide(InstrumentationRegistry.getInstrumentation().getTargetContext());
	}

	@Override protected void after() {
		GlideResetter.resetGlide(InstrumentationRegistry.getInstrumentation().getTargetContext());
	}
}
