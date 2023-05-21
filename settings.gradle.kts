import net.twisterrob.gradle.doNotNagAbout

rootProject.name = "net-twisterrob-libraries"

// TODO enable when AGP 8, see https://github.com/gradle/android-cache-fix-gradle-plugin/issues/466
//enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
	@Suppress("UnstableApiUsage")
	includeBuild("gradle/plugins")
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
	// TODO when twisterrob-settings add
	//disableLoggingFor("org.gradle.configurationcache.problems.ConfigurationCacheProblems") 
}

plugins {
	id("net.twisterrob.gradle.plugin.settings") version "0.15.1"
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
includeAndroid(":cpsuite")
includeAndroid(":uiautomator")
includeAndroid(":annotations")
includeAndroid(":defs")
includeAndroid(":basics")
includeAndroid(":permissions")

includeAndroid(":glide3")
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

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-301430, fixed in AS Giraffe.
if ((System.getProperty("idea.version") ?: "") < "2022.3") {
	doNotNagAbout(
		"The org.gradle.util.GUtil type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#org_gradle_util_reports_deprecations",
		//"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
	)
} else {
	error("Android Studio version changed, please remove hack.")
}

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-284158
if ((System.getProperty("idea.version") ?: "") < "2023.2") {
	doNotNagAbout(
		"Resolution of the configuration :tools:transform:detachedConfiguration1 " +
			"was attempted from a context different than the project context. " +
			"Have a look at the documentation to understand why this is a problem and how it can be resolved. " +
			"This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
			"See https://docs.gradle.org/${gradleVersion}/userguide/viewing_debugging_dependencies.html#sub:resolving-unsafe-configuration-resolution-errors for more details.",
		//	at org.jetbrains.plugins.gradle.model.ProjectImportAction$MyBuildController.findModel(ProjectImportAction.java:618)
	)
} else {
	error("Android Studio version changed, please review hack.")
}
