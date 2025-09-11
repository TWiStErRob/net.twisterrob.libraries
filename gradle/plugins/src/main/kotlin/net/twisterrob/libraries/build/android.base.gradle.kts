package net.twisterrob.libraries.build

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.TestedExtension
import com.android.build.api.variant.HasAndroidTestBuilder
import net.twisterrob.libraries.build.dsl.android
import net.twisterrob.libraries.build.dsl.androidComponents
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

android {
	namespace = project.autoNamespace
	compileSdk = 35
	defaultConfig {
		minSdk = 21
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
}

androidComponents {
	val testBuildType = (android as? TestedExtension)?.testBuildType
	beforeVariants {
		if (it.buildType != testBuildType) return@beforeVariants
		if (it !is HasAndroidTestBuilder) return@beforeVariants
		val androidTest = android.sourceSets.named("androidTest").get()
		if (androidTest.javaSources.isEmpty && androidTest.kotlinSources.isEmpty) {
			logger.info(
				"Disabling ${it.name} androidTest variant in ${project.path}" +
						" as it has no sources in ${androidTest.srcDirs}"
			)
			it.androidTest.enable = false
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
