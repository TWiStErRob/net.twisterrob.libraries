pluginManagement {
	includeBuild("gradle/plugins")
	repositories {
		google()
		mavenCentral()
	}
}

includeBuild("twister-lib-java") {
	name = "twister-libs-java"
	apply(from = projectDir.resolve("settings.substitutions.gradle"), to = this)
}

includeBuild("twister-lib-android") {
	name = "twister-libs-android"
	apply(from = projectDir.resolve("settings.substitutions.gradle"), to = this)
}
