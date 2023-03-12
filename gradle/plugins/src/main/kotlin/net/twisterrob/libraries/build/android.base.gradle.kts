package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.android
import net.twisterrob.libraries.build.dsl.autoNamespace

repositories {
	google()
	mavenCentral()
}

@Suppress("UnstableApiUsage")
android {
	namespace = project.autoNamespace
	compileSdk = 33
	defaultConfig {
		minSdk = 14
	}
	buildFeatures {
		buildConfig = false
	}
	lint {
		warningsAsErrors = true
		checkAllWarnings = true
		lintConfig = rootDir.resolve("twister-lib-android/config/lint/lint.xml")
		baseline = rootDir.resolve("twister-lib-android/config/lint/lint-baseline-${project.name}.xml")
	}
	afterEvaluate {
		sourceSets.named("androidTest").configure androidTest@{
			@Suppress("DEPRECATION") // REPORT cannot replace with new interface, missing methods
			this@androidTest as com.android.build.gradle.api.AndroidSourceSet
			if (this@androidTest.java.getSourceFiles().isEmpty) {
				logger.info(
					"Disabling AndroidTest tasks in ${project.path}" +
						" as it has no sources in ${this@androidTest.java.srcDirs}"
				)
				tasks.configureEach {
					if (this.name.contains("AndroidTest")) {
						this.enabled = false
					}
				}
			}
		}
	}
}
