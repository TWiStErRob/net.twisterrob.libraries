package net.twisterrob.libraries.build

import com.android.build.gradle.options.BooleanOption
import com.android.build.gradle.options.ProjectOptions

plugins {
	id("net.twisterrob.gradle.plugin.android-library")
}

android {
	defaultConfig {
		multiDexEnabled = true
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

/*
 * Work around two interacting AGP 9.4.1 / UTP defects exposed by API 21 devices.
 *
 * REPORT UTP's host-side additional-output collection runs `am get-current-user`, which API 21 does not
 * support. Its `am` usage text is then treated as the user ID in a `/storage/emulated/...` path,
 * producing an unbounded `adb pull` error path such as `/storage/emulated/usage: am ...`.
 * Creating the requested directory in an instrumentation runner cannot help: path resolution and
 * the failing pull happen on the host after instrumentation. Consequently, root gradle.properties
 * disables android.enableAdditionalTestOutput for this repository.
 *
 * REPORT Disabling the option exposes a second AGP defect: task creation leaves additionalTestOutputDir
 * unset, but DeviceProviderInstrumentTestTask.doTaskAction() still calls get() on it unconditionally.
 * Give the disabled feature a harmless local directory so the task can execute without enabling
 * UTP's broken device-output collection. Remove this when AGP handles both the disabled option and
 * API 21 path resolution correctly.
 */
val enableAdditionalTestOutput = ProjectOptions(providers).get(BooleanOption.ENABLE_ADDITIONAL_ANDROID_TEST_OUTPUT)
if (!enableAdditionalTestOutput) {
	@Suppress("DEPRECATION")
	tasks.withType<com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask>()
		.configureEach {
			additionalTestOutputDir = project.layout.buildDirectory.dir("tmp/disabledAdditionalTestOutput/${name}")
		}
}
