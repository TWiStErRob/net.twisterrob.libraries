package net.twisterrob.android.test.matchers;

import java.util.*;

import org.hamcrest.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

public class HasInstalledPackage extends TypeSafeDiagnosingMatcher<Context> {

	private final String packageName;

	/**
	 * @deprecated Use {@link #hasInstalledPackage(String)} instead.
	 */
	// TODO replace with android.Manifest.permission.QUERY_ALL_PACKAGES
	@RequiresPermission("android.permission.QUERY_ALL_PACKAGES")
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public HasInstalledPackage(String packageName) {
		this.packageName = packageName;
	}

	@Override public void describeTo(Description description) {
		description.appendValue(packageName).appendText(" package installed");
	}

	// TODO replace with android.Manifest.permission.QUERY_ALL_PACKAGES
	@RequiresPermission("android.permission.QUERY_ALL_PACKAGES")
	@Override protected boolean matchesSafely(Context context, Description mismatchDescription) {
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			@SuppressLint("QueryPermissionsNeeded") // Used from tests, so we can request QUERY_ALL_PACKAGES.
			Iterable<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
			Collection<String> packageNames = new ArrayList<>();
			for (PackageInfo aPackage : packages) {
				packageNames.add(aPackage.packageName);
			}
			mismatchDescription.appendValueList(
					"The following packages were installed: ", ", ", ".", packageNames);
			return false;
		}

		if (!info.applicationInfo.enabled) {
			mismatchDescription.appendValue(packageName).appendText(" is disabled");
			return false;
		}

		return true;
	}

	// TODO replace with android.Manifest.permission.QUERY_ALL_PACKAGES
	@RequiresPermission("android.permission.QUERY_ALL_PACKAGES")
	public static @NonNull Matcher<Context> hasInstalledPackage(String packageName) {
		return new HasInstalledPackage(packageName);
	}
}
