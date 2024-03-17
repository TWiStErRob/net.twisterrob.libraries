plugins {
	id("net.twisterrob.libraries.build.allprojects")
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.libraries.build.android.library")
	id("net.twisterrob.libraries.build.android.base")
	id("net.twisterrob.libraries.build.android.test")
	id("org.jetbrains.kotlin.android")
}

project.findProject("${project.path}-test_helpers")?.let { testHelpers ->
	dependencies {
		androidTestImplementation(testHelpers)
	}
}

afterEvaluate {
	configurations.named("${android.testBuildType}AndroidTestRuntimeClasspath") {
		// Undo com.android.build.gradle.internal.dependency.VariantDependenciesBuilder#maybeAddDependencyConstraints:
		// `runtimeClasspath.shouldResolveConsistentlyWith(testedRuntimeClasspath)`
		// REPORT 8.2.2 -> 8.3.0 It is useful, but makes guava vs listenablefuture unresolvable.
		// To reproduce, comment the next line and `gradlew assembleDebugAndroidTest`.
		disableConsistentResolution()
	}
}
