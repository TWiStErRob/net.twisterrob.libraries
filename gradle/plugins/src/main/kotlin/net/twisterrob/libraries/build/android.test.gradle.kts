package net.twisterrob.libraries.build

plugins {
	id("net.twisterrob.android-library")
}

android {
	defaultConfig {
		multiDexEnabled = true
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}
