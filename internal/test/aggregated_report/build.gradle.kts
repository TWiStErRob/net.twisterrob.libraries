import com.android.build.gradle.internal.test.tasks.TestReportTask

/*
 * Source-less dependency graph root for AGP's experimental unified test report.
 * android.experimental.reportAggregationSupport makes AGP consume TEST_RESULTS artifacts from runtime dependencies.
 * No normal library represents the whole repository graph, so this dedicated module provides that root.
 *
 * To create a report based on existing results on disk without executing tests run:
 * ```shell
 * gradlew :internal:test:aggregated_report:createAggregatedTestReport -x connectedDebugAndroidTest -x testDebugUnitTest
 * ```
 */

plugins {
	id("net.twisterrob.libraries.android.library")
}

android {
	publishing {
		// For a library, AGP configures the aggregate reporting only when a variant has a publishing component.
		singleVariant("debug")
	}
	lint {
		// This module is not meant to be consumed by anyone,
		// it aggregates all kinds of modules including test-only libraries to be executed on JVM.
		disable.add("InvalidPackage")
	}
}

// TODEL https://github.com/TWiStErRob/github-workflows/issues/98
// Temporary workaround: the reusable workflow only uploads merged reports from this directory.
tasks.withType<TestReportTask>().named { it == "createAggregatedTestReport" }.configureEach {
	testReport = rootProject.layout.buildDirectory.dir("androidTest-results")
}

repositories {
	// Gradle resolves the complete runtime graph before AGP selects project TEST_RESULTS artifacts.
	// :color_picker contributes this one JitPack dependency, so the graph root must be able to resolve it too.
	maven {
		name = "jitpack.io"
		url = uri("https://jitpack.io/")
		content {
			includeModule(libs.android.colorpicker.get().module.group, libs.android.colorpicker.get().module.name)
		}
	}
}

dependencies {
	// Only Android projects publish the TEST_RESULTS artifacts consumed by AGP,
	// but we can't know what plugins each other project applies.
	rootProject.allprojects
		// This project cannot depend on itself.
		.filter { it != project }
		// Depend on every leaf project automatically; structural projects have no consumable variant.
		.filter { it.childProjects.isEmpty() }
		// Aggregation follows this module's runtime graph.
		.forEach { runtimeOnly(project(it.path)) }
}

dependencyAnalysis {
	issues {
		onUnusedDependencies {
			// These dependencies model the runtime graph consumed by AGP's report aggregation, not source usage.
			severity("ignore")
		}
	}
}
