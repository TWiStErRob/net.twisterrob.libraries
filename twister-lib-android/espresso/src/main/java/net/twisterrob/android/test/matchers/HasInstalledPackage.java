package net.twisterrob.android.test.matchers;

import java.util.*;

import org.hamcrest.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.RequiresPermission;

import net.twisterrob.android.utils.tools.PackageManagerTools;

/**
 * This requires the caller to hold the {@link Manifest.permission#QUERY_ALL_PACKAGES} permission.
 * Or explicitly list the passed-in package name(s) in the manifest via {@code <queries>}.
 */
public class HasInstalledPackage extends TypeSafeDiagnosingMatcher<Context> {

	private final String packageName;

	/**
	 * @see AndroidMatchers#hasPackageInstalled(String)
	 */
	@SuppressLint("InlinedApi")
	@RequiresPermission(Manifest.permission.QUERY_ALL_PACKAGES)
	public HasInstalledPackage(String packageName) {
		this.packageName = packageName;
	}

	@Override public void describeTo(Description description) {
		description.appendValue(packageName).appendText(" package installed");
	}

	@SuppressLint("InlinedApi")
	@RequiresPermission(Manifest.permission.QUERY_ALL_PACKAGES)
	@Override protected boolean matchesSafely(Context context, Description mismatchDescription) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info;
		try {
			info = PackageManagerTools.getPackageInfo(pm, packageName, 0);
		} catch (NameNotFoundException e) {
			@SuppressLint("QueryPermissionsNeeded") // Used from tests, so we can request QUERY_ALL_PACKAGES.
			Iterable<PackageInfo> packages = PackageManagerTools.getInstalledPackages(pm, 0);
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
}
