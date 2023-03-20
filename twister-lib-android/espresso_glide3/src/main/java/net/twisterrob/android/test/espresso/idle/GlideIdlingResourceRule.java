package net.twisterrob.android.test.espresso.idle;

import net.twisterrob.android.test.junit.IdlingResourceRule;

public class GlideIdlingResourceRule extends IdlingResourceRule {
	public GlideIdlingResourceRule() {
		super(new GlideIdlingResource());
	}
}
