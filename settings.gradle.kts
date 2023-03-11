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

include(":internal")
include(":internal:test")
include(":internal:test:jvm_unit")
include(":internal:test:android_unit")
include(":internal:test:android_instrumentation")

includeJava(":utils")
includeJava(":utils:stringer")
includeJava(":utils:core")
includeJava(":utils:collect")
includeJava(":utils:test")
includeJava(":lib")
includeJava(":lib:general")
includeJava(":lib:java")
includeJava(":lib:java_desktop")
includeJava(":lib:junit4")
includeJava(":lib:hamcrest")
includeJava(":lib:mockito")

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
