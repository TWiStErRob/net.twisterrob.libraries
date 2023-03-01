package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.android

repositories {
	google()
	mavenCentral()
}

apply(from = rootProject.file("gradle/substitutions.gradle"), to = project)

@Suppress("UnstableApiUsage")
android {
	compileSdk = 28
	defaultConfig {
		minSdk = 14
	}
	buildFeatures {
		buildConfig = false
	}
	lintOptions {
		isWarningsAsErrors = true
		isCheckAllWarnings = true
		lintConfig = rootProject.file("config/lint/lint.xml")
		baselineFile = rootProject.file("config/lint/lint-baseline-${project.name}.xml")
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
