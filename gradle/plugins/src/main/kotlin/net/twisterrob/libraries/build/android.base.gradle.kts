package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.android

repositories {
	google()
	mavenCentral()
}

configurations.configureEach {
	if (this.name == "lintClassPath") return@configureEach
}
apply(from = rootProject.file("gradle/substitutions.gradle"), to = project)

plugins.withId("net.twisterrob.android-library") {
	android.buildFeatures.buildConfig = false
}

android.compileSdk = 28
android.defaultConfig.minSdk = 14
android.lintOptions.isWarningsAsErrors = true
android.lintOptions.isCheckAllWarnings = true
android.lintOptions.lintConfig = rootProject.file("config/lint/lint.xml")
android.lintOptions.baselineFile = rootProject.file("config/lint/lint-baseline-${project.name}.xml")

@Suppress("DEPRECATION") // REPORT cannot replace with new interface, missing methods
val androidTest = project.android.sourceSets["androidTest"] as com.android.build.gradle.api.AndroidSourceSet
if (androidTest.java.getSourceFiles().isEmpty) {
	logger.info("Disabling AndroidTest tasks in ${project.path} as it has no sources in ${androidTest.java.srcDirs}")
	tasks.configureEach {
		if (this.name.contains("AndroidTest")) {
			this.enabled = false
		}
	}
}
