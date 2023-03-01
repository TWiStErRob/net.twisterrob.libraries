rootProject.name = "twister-libs-android"

pluginManagement {
	includeBuild("../gradle/plugins")
	repositories {
		google()
		mavenCentral()
	}
}

include(":")

include(":monolith")

include(":slf4j")
include(":logging")
include(":stringers")

include(":espresso")
include(":espresso_actors")
include(":espresso_glide3")
include(":cpsuite")
include(":uiautomator")
include(":annotations")
include(":defs")
include(":basics")

include(":glide3")
include(":widgets")
include(":settings")

includeWithTestHelpers(":about")
includeWithTestHelpers(":capture_image")

includeBuild("../twister-lib-java") {
	name = "twister-libs-java"
	apply(from = projectDir.resolve("settings.substitutions.gradle"), to = this)
}

fun Settings.includeWithTestHelpers(modulePath: String) {
	include(modulePath)
	val testHelpersModulePath = "${modulePath}-test_helpers"
	include(testHelpersModulePath)
	val testHelpersModule = project(testHelpersModulePath)
	testHelpersModule.projectDir = project(modulePath).projectDir.resolve("test_helpers")
}
