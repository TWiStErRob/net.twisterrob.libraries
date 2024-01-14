package net.twisterrob.android.test.matchers;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Answers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.NonNull;

import net.twisterrob.android.utils.tools.PackageManagerTools;

public class HasInstalledPackageTest {

	// mocked unit test because pm disable is system-level
	@Test public void disabledPackageFails() throws NameNotFoundException {
		final Context context = mock(Context.class, Answers.RETURNS_DEEP_STUBS);
		PackageManager pm = context.getPackageManager();
		when(PackageManagerTools.getPackageInfo(pm, anyString(), anyInt()))
				.thenReturn(createDisabled());
		AssertionError ex = assertThrows(AssertionError.class, new ThrowingRunnable() {
			@Override public void run() {
				assertThat(context, new HasInstalledPackage("existing.package"));
			}
		});
		assertThat(ex.getMessage(), allOf(
				containsString("existing.package"),
				containsString("disabled")
		));
	}

	private @NonNull PackageInfo createDisabled() {
		PackageInfo packageInfo = new PackageInfo();
		packageInfo.applicationInfo = new ApplicationInfo();
		packageInfo.applicationInfo.enabled = false;
		return packageInfo;
	}
}
