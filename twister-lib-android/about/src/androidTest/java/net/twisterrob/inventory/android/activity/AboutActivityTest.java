package net.twisterrob.inventory.android.activity;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.NonNull;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import net.twisterrob.android.about.R;
import net.twisterrob.android.activity.AboutActivity;
import net.twisterrob.android.test.junit.SensibleActivityTestRule;
import net.twisterrob.android.utils.tools.PackageManagerTools;
import net.twisterrob.inventory.android.test.actors.AboutActivityActor;

import static net.twisterrob.android.test.matchers.AndroidMatchers.containsStringRes;
import static net.twisterrob.android.test.matchers.AndroidMatchers.stringRes;

//@Category(On.Support.class)
public class AboutActivityTest {
	@SuppressWarnings("deprecation")
	@Rule public final androidx.test.rule.ActivityTestRule<AboutActivity> activity =
			new SensibleActivityTestRule<>(AboutActivity.class);
	private final AboutActivityActor about = new AboutActivityActor();

//	@Category({Op.Rotates.class})
	@Test public void testRotate() {
		about.rotate();
		about.rotate();
	}

//	@Category(UseCase.InitialCondition.class)
	@Test public void testAppPackageShown() {
		about.assertTextExists(containsString(getApplicationContext().getPackageName()));
	}

//	@Category(UseCase.InitialCondition.class)
	@Test public void testAppNameShown() {
		int appName = net.twisterrob.android.about.test.R.string.about_test_application_label;
		about.assertTextExists(containsStringRes(appName));
	}

//	@Category(UseCase.InitialCondition.class)
	@Test public void testAppVersionShown() {
		about.assertTextExists(containsString(getPackageInfo().versionName));
		long versionCode = PackageManagerTools.getVersionCode(getPackageInfo());
		about.assertTextExists(containsString(String.valueOf(versionCode)));
	}

//	@Category(UseCase.InitialCondition.class)
	@Test public void testLicencesMentioned() {
		String[] licences = getApplicationContext().getResources().getStringArray(R.array.about_licenses);
		for (String licence : licences) {
			about.assertTextExists(equalTo(licence));
		}
	}

//	@Category(UseCase.InitialCondition.class)
	@Test public void testSectionsShown() {
		about.assertTextExists(equalTo(stringRes(R.string.about_faq_title)));
		about.assertTextExists(equalTo(stringRes(R.string.about_help_title)));
		about.assertTextExists(equalTo(stringRes(R.string.about_tips_title)));
	}

	private static @NonNull PackageInfo getPackageInfo() {
		Context context = getApplicationContext();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = PackageManagerTools.getPackageInfo(pm, context.getPackageName(), 0);
			assertThat(context.getPackageName(), info, not(nullValue()));
			return info;
		} catch (NameNotFoundException e) {
			AssertionError fail = new AssertionError("Cannot get package info for " + context.getPackageName());
			//noinspection UnnecessaryInitCause API 19 only
			fail.initCause(e);
			throw fail;
		}
	}
}
