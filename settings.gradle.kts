import net.twisterrob.gradle.doNotNagAbout

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

val gradleVersion = GradleVersion.current().version

// TODEL Gradle 8.8 sync in IDEA 2024.1.4 https://youtrack.jetbrains.com/issue/IDEA-353787.
if ((System.getProperty("idea.version") ?: "") < "2024.3") {
	doNotNagAbout(
		"The CopyProcessingSpec.getFileMode() method has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the getFilePermissions() method instead. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#unix_file_permissions_deprecated",
		// Ideally this would be the following:
		// "at com.intellij.gradle.toolingExtension.impl.model.resourceFilterModel.GradleResourceFilterModelBuilder.getFilters(GradleResourceFilterModelBuilder.groovy:46)"
		// but org.gradle.internal.classpath.intercept.DefaultCallSiteDecorator$DecoratingCallSite is using up too many stack frames.
		"at org.gradle.internal.classpath.InstrumentedGroovyCallsHelper.withEntryPoint(InstrumentedGroovyCallsHelper.java:34)"
	)
	doNotNagAbout(
		"The CopyProcessingSpec.getDirMode() method has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the getDirPermissions() method instead. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#unix_file_permissions_deprecated",
		// Ideally this would be the following:
		// "at com.intellij.gradle.toolingExtension.impl.model.resourceFilterModel.GradleResourceFilterModelBuilder.getFilters(GradleResourceFilterModelBuilder.groovy:46)"
		// but org.gradle.internal.classpath.intercept.DefaultCallSiteDecorator$DecoratingCallSite is using up too many stack frames.
		"at org.gradle.internal.classpath.InstrumentedGroovyCallsHelper.withEntryPoint(InstrumentedGroovyCallsHelper.java:34)"
	)
} else {
	logger.warn("IDEA version changed, please review hack.")
}
