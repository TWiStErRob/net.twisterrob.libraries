import com.android.build.api.variant.HasHostTestsBuilder

plugins {
	id("com.android.application")
	id("net.twisterrob.libraries.build.allprojects")
	id("net.twisterrob.libraries.build.android.base")
}

/*
 * AGP's unified multi-module test report follows the runtime dependency graph of an Android application.
 * This repository only publishes sibling Android libraries,
 * so none of its normal modules represents the whole graph and AGP has nowhere to register createAggregatedTestReport.
 *
 * This source-less application exists solely to provide that graph root.
 * Keep every Android library as a direct runtime dependency:
 * AGP consumes their published TEST_RESULTS artifacts and creates its native dashboard at build/reports/tests/aggregated-test-report.
 * The feature is still experimental in AGP 9.4.1 and is enabled by android.experimental.reportAggregationSupport.
 *
 * The native report task normally executes every unit and connected test that produces an input;
 * AGP deliberately marks connected tests never up-to-date because device state is not modeled.
 * CI requests connectedCheck and the aggregate report together,
 * so Gradle executes their shared test producers once.
 * For a local re-render after running any subset of modules, use:
 *
 *     gradlew :test_report:createAggregatedTestReport -x connectedDebugAndroidTest -x testDebugUnitTest
 *
 * Exclusion is intentional here: the enriched XML is written by each test task when report aggregation is enabled,
 * so AGP's collection and HTML tasks can safely consume earlier outputs.
 */
android {
	namespace = "net.twisterrob.libraries.test.report"
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
	}
}

androidComponents.beforeVariants {
	(it as HasHostTestsBuilder).hostTests.values.forEach { hostTest ->
		hostTest.enable = false
	}
}

repositories {
	maven {
		name = "jitpack.io"
		url = uri("https://jitpack.io/")
		content {
			includeModule("com.github.martin-stone", "hsv-alpha-color-picker-android")
		}
	}
}

dependencies {
	coreLibraryDesugaring(libs.android.desugar)

	implementation(project(":internal:test:android_unit"))
	implementation(project(":internal:test:android_instrumentation"))
	implementation(project(":monolith"))
	implementation(project(":slf4j"))
	implementation(project(":slf4j-test_helpers"))
	implementation(project(":logging"))
	implementation(project(":stringers"))
	implementation(project(":espresso"))
	implementation(project(":espresso_actors"))
	implementation(project(":espresso_glide3"))
	implementation(project(":espresso_glide4"))
	implementation(project(":cpsuite"))
	implementation(project(":uiautomator"))
	implementation(project(":annotations"))
	implementation(project(":defs"))
	implementation(project(":basics"))
	implementation(project(":permissions"))
	implementation(project(":color_picker"))
	implementation(project(":glide3"))
	implementation(project(":glide4"))
	implementation(project(":orbit"))
	implementation(project(":mad"))
	implementation(project(":widgets"))
	implementation(project(":settings"))
	implementation(project(":about"))
	implementation(project(":about-test_helpers"))
	implementation(project(":capture_image"))
	implementation(project(":capture_image-test_helpers"))
}
