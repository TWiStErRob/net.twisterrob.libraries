rootProject.name = "twister-libs-android"

pluginManagement {
	includeBuild("../gradle/plugins")
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
	val contractModulePath = "${modulePath}-test_helpers"

	include(modulePath)
	val module = project(modulePath)

	include(contractModulePath)
	val contractModule = project(contractModulePath)

	contractModule.projectDir = module.projectDir.resolve("test_helpers")
}
