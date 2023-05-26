package net.twisterrob.libraries.build

import com.android.build.api.dsl.AndroidSourceSet
import net.twisterrob.libraries.build.dsl.android
import net.twisterrob.libraries.build.dsl.autoNamespace

repositories {
	google()
	mavenCentral()
}

dependencies {
	// Need to use ""() notation, because neither library, nor application plugin applied on this convention.
	"implementation"(platform("net.twisterrob.libraries.build:platform-libs"))
	"testImplementation"(platform("net.twisterrob.libraries.build:platform-libs"))
	"androidTestImplementation"(platform("net.twisterrob.libraries.build:platform-libs"))
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
			if (this@androidTest.javaSources.isEmpty && this@androidTest.kotlinSources.isEmpty) {
				logger.info(
					"Disabling AndroidTest tasks in ${project.path}" +
						" as it has no sources in ${this@androidTest.srcDirs}"
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

val AndroidSourceSet.javaSources: FileTree
	@Suppress("DEPRECATION") // REPORT cannot replace with new interface, missing methods
	get() = (this as com.android.build.gradle.api.AndroidSourceSet).java.getSourceFiles()

val AndroidSourceSet.kotlinSources: FileTree
	@Suppress("DEPRECATION") // REPORT cannot replace with new interface, missing methods
	get() = (this.kotlin as com.android.build.gradle.api.AndroidSourceDirectorySet).getSourceFiles()

val AndroidSourceSet.srcDirs: Set<File>
	@Suppress("DEPRECATION") // REPORT cannot replace with new interface, missing methods
	get() = (this as com.android.build.gradle.api.AndroidSourceSet).java.srcDirs +
		(this.kotlin as com.android.build.gradle.api.AndroidSourceDirectorySet).srcDirs
