includeBuild("twister-lib-java") {
	@Suppress("UnstableApiUsage")
	name = "twister-libs-java"
	apply(from = projectDir.resolve("settings.substitutions.gradle"), to = this)
}
includeBuild("twister-lib-android") {
	@Suppress("UnstableApiUsage")
	name = "twister-libs-android"
	apply(from = projectDir.resolve("settings.substitutions.gradle"), to = this)
}
