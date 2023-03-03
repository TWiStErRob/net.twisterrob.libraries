package net.twisterrob.libraries.build

plugins {
	id("com.android.lint")
}

lint {
	warningsAsErrors = true
	checkAllWarnings = true
	lintConfig = rootProject.file("twister-lib-java/config/lint/lint.xml")
	baseline = rootProject.file("twister-lib-java/config/lint/lint-baseline-${project.name}.xml")
	
	// lint:CannotEnableHidden Follow net.twisterrob.gradle.android.AndroidBuildPlugin.configureLint.
	// > Issue StopShip was configured with severity fatal in monolith,
	// > but was not enabled (or was disabled) in library *
	fatal.add("StopShip")
}
