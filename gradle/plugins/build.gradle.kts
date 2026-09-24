plugins {
	`kotlin-dsl`
	alias(libs.plugins.detekt)
}

dependencies {
	implementation(libs.plugins.kotlin.asMarkerArtifact())
	implementation(libs.plugins.ksp.asMarkerArtifact())
	implementation(libs.plugins.android.asMarkerArtifact())
	implementation(libs.plugins.twisterrob.quality.asMarkerArtifact())
	implementation(libs.plugins.twisterrob.android.asMarkerArtifact())
	implementation(libs.plugins.dependencyAnalysis.asMarkerArtifact())
	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

kotlin {
	compilerOptions {
		allWarningsAsErrors = true
	}
}

tasks.named("pluginDescriptors").configure {
	finalizedBy("validatePlugins")
}

tasks.withType<ValidatePlugins>().configureEach {
	ignoreFailures = false
	failOnWarning = true
	enableStricterValidation = true
}

detekt {
	buildUponDefaultConfig = true
	allRules = true
	parallel = true
	config.from("../../config/detekt/detekt.yml")
}

fun Provider<PluginDependency>.asMarkerArtifact(): Provider<String> = map {
	"${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}
