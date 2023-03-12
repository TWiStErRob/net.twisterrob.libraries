package net.twisterrob.libraries.build

plugins {
	id("net.twisterrob.gradle.plugin.android-library")
}

android {
	defaultConfig {
		multiDexEnabled = true
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}
