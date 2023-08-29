plugins {
	id("net.twisterrob.libraries.build.allprojects")
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.libraries.build.android.library")
	id("net.twisterrob.libraries.build.android.base")
	id("org.jetbrains.kotlin.android")
}

dependencies {
	api(project(project.path.removeSuffix("-test_helpers")))
}
