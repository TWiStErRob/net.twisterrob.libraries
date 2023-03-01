plugins {
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.libraries.build.android.library")
	id("net.twisterrob.libraries.build.android.base")
	id("net.twisterrob.libraries.build.android.test")
}

project.findProject("${project.path}-test_helpers")?.let { testHelpers ->
	dependencies {
		androidTestImplementation(testHelpers)
	}
}
