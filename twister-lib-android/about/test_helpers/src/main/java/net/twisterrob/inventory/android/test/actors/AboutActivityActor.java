package net.twisterrob.inventory.android.test.actors;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import net.twisterrob.android.activity.AboutActivity;

import static net.twisterrob.android.test.espresso.EspressoExtensions.exists;

public class AboutActivityActor extends ActivityActor {
	public AboutActivityActor() {
		super(AboutActivity.class);
	}
	
	public void assertTextExists(Matcher<String> matcher) {
		onView(withText(matcher))/*.perform(scrollTo())*/.check(exists());
	}
}
