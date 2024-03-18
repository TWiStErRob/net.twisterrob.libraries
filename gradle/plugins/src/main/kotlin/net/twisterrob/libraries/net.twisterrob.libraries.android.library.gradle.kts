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
		// TODEL https://issuetracker.google.com/issues/330202422
		resolutionStrategy.force("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
	}
}
