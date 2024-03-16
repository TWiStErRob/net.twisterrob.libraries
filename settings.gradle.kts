import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.libraries.build.dsl.isCI

rootProject.name = "net-twisterrob-libraries"

// TODO enable when https://issuetracker.google.com/issues/300617088
//enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
	includeBuild("gradle/plugins")
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("net.twisterrob.gradle.plugin.settings") version "0.16"
	id("net.twisterrob.libraries.settings")
}

dependencyResolutionManagement {
	includeBuild("gradle/platform-libs")
}

include(":internal")
include(":internal:test")
include(":internal:test:jvm_unit")
include(":internal:test:android_unit")
include(":internal:test:android_instrumentation")

// Note: the container project is already relocated, any subprojects will be resolved relative to it.
includeJava(":utils")
include(":utils:stringer")
include(":utils:core")
include(":utils:collect")
include(":utils:test")
includeJava(":lib")
include(":lib:general")
include(":lib:java")
include(":lib:java_desktop")
include(":lib:junit4")
include(":lib:hamcrest")
include(":lib:mockito")

includeAndroid(":monolith")

includeAndroid(":slf4j")
includeAndroid(":logging")
includeAndroid(":stringers")

includeAndroid(":espresso")
includeAndroid(":espresso_actors")
includeAndroid(":espresso_glide3")
includeAndroid(":espresso_glide4")
includeAndroid(":cpsuite")
includeAndroid(":uiautomator")
includeAndroid(":annotations")
includeAndroid(":defs")
includeAndroid(":basics")
includeAndroid(":permissions")

includeAndroid(":color_picker")
includeAndroid(":glide3")
includeAndroid(":glide4")
includeAndroid(":orbit")
includeAndroid(":mad")
includeAndroid(":widgets")
includeAndroid(":settings")

includeAndroidWithTestHelpers(":about")
includeAndroidWithTestHelpers(":capture_image")

fun Settings.includeJava(modulePath: String) {
	include(modulePath)
	val module = project(modulePath)
	module.projectDir = file("twister-lib-java").resolve(module.projectDir.relativeTo(rootDir))
}

fun Settings.includeAndroid(modulePath: String) {
	include(modulePath)
	val module = project(modulePath)
	module.projectDir = file("twister-lib-android").resolve(module.projectDir.relativeTo(rootDir))
}

fun Settings.includeAndroidWithTestHelpers(modulePath: String) {
	includeAndroid(modulePath)
	val testHelpersModulePath = "${modulePath}-test_helpers"
	includeAndroid(testHelpersModulePath)
	val testHelpersModule = project(testHelpersModulePath)
	testHelpersModule.projectDir = project(modulePath).projectDir.resolve("test_helpers")
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle 8.2 sync in AS FL 2022.2.1 / AS GI 2022.3.1 / IDEA 2023.1 / AS HH 2023.2.1, last piece fixed in 2023.3.
// https://youtrack.jetbrains.com/issue/IDEA-320266
@Suppress("MaxLineLength")
if ((System.getProperty("idea.version") ?: "") < "2023.3") {
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
} else {
	val error: (String) -> Unit = if (isCI) ::error else logger::warn
	error("Android Studio version changed, please review convention hack.")
}

// TODEL Gradle 8.2 sync in AS FL https://youtrack.jetbrains.com/issue/IDEA-320307
@Suppress("MaxLineLength", "StringLiteralDuplication")
if ((System.getProperty("idea.version") ?: "") < "2024.1") {
	doNotNagAbout(
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		// There are multiple stack traces coming to this line, ignore them all at once.
		"at org.jetbrains.plugins.gradle.tooling.util.resolve.DependencyResolverImpl.resolveDependencies(DependencyResolverImpl.java:266)"
	)
} else {
	val error: (String) -> Unit = if (isCI) ::error else logger::warn
	error("Android Studio version changed, please review BuildIdentifier hack.")
}
