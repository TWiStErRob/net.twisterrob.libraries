package net.twisterrob.libraries.build

plugins {
	id("net.twisterrob.gradle.plugin.android-library")
}

android {
	twisterrob {
		isDecorateBuildConfig = false
	}
}
