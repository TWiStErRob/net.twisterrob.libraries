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
	compileSdk = 28
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

tasks.withType<JavaCompile>().configureEach javac@{
	this@javac.options.compilerArgs = this@javac.options.compilerArgs + listOf(
		// Google's compilers emit some weird stuff (espresso, dagger, etc.)
		// warning: [classfile] MethodParameters attribute introduced in version 52.0 class files
		// is ignored in version 51.0 class files
		"-Xlint:-classfile",
	)
}
