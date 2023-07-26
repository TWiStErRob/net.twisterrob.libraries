plugins {
	`kotlin-dsl`
	alias(libs.plugins.detekt)
}

dependencies {
	implementation(libs.plugin.kotlin.gradle)
	implementation(libs.plugin.android.gradle)
	implementation(libs.plugin.twisterrob.quality)
	implementation(libs.plugin.twisterrob.android)
	implementation(libs.plugin.dependencyAnalysis)
	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		allWarningsAsErrors = true
	}
}

tasks.named("pluginDescriptors").configure {
	finalizedBy("validatePlugins")
}

tasks.withType<ValidatePlugins>().configureEach {
	ignoreFailures.set(false)
	failOnWarning.set(true)
	enableStricterValidation.set(true)
}

detekt {
	allRules = true
	parallel = true
}
