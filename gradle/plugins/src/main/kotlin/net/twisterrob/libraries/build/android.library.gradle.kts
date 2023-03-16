package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("net.twisterrob.gradle.plugin.android-library")
}

android {
	twisterrob {
		isDecorateBuildConfig = false
	}
}

dependencies {
	// Lock in the version of Kotlin used so that the transitive dependencies are consistently upgraded.
	// https://kotlinlang.org/docs/whatsnew18.html#usage-of-the-latest-kotlin-stdlib-version-in-transitive-dependencies
	implementation(platform(libs.kotlin.bom))
	constraints {
		implementation(libs.androidx.lifecycleViewModel)
	}
}
