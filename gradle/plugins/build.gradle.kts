plugins {
	`kotlin-dsl`
	alias(libs.plugins.detekt)
}

dependencies {
	implementation(libs.plugins.kotlin.asMarkerArtifact())
	implementation(libs.plugins.android.asMarkerArtifact())
	implementation(libs.plugins.twisterrob.quality.asMarkerArtifact())
	implementation(libs.plugins.twisterrob.android.asMarkerArtifact())
	implementation(libs.plugins.dependencyAnalysis.asMarkerArtifact())
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

fun Provider<PluginDependency>.asMarkerArtifact(): Provider<String> = map {
	"${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}
