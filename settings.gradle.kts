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

includeAndroidWithTestHelpers(":slf4j")
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

// TODEL Gradle 8.13 vs AGP 8.0-8.9 https://issuetracker.google.com/issues/370546370
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'crunchPngs' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getCrunchPngs' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated.isCrunchPngs' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'useProguard' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.gradle.internal.dsl.BuildType has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getUseProguard' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType.isUseProguard' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'wearAppUnbundled' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.api.variant.impl.ApplicationVariantImpl has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getWearAppUnbundled' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.api.variant.impl.ApplicationVariantImpl.isWearAppUnbundled' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)

// TODEL Gradle 8.14 vs AGP 8.9 https://issuetracker.google.com/issues/408334529
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Retrieving attribute with a null key. " +
			"This behavior has been deprecated. " +
			"This will fail with an error in Gradle 10. " +
			"Don't request attributes from attribute containers using null keys. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#null-attribute-lookup",
	"at com.android.build.gradle.internal.ide.dependencies.ArtifactUtils.isAndroidProjectDependency(ArtifactUtils.kt:539)",
)
