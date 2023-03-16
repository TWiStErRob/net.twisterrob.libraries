plugins {
	id("net.twisterrob.libraries.root")
	id("net.twisterrob.libraries.build.dependencyAnalysis")
	id("com.github.ben-manes.versions") version "0.46.0"
	id("nl.littlerobots.version-catalog-update") version "0.8.0"
}
versionCatalogUpdate {
	sortByKey.set(false)
//	keepUnusedVersions = true
//	keepUnusedLibraries = true
//	keepUnusedPlugins = true
}
fun isNonStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)
	return isStable.not()
}
tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates").configure {
	rejectVersionIf {
		isNonStable(candidate.version)
	}
}
// :plugins:check is not automatically invoked when doing `gradlew check`. Help Gradle discover it.
tasks.named("check") {
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

// :plugins:build is not automatically invoked when doing `gradlew build`. Help Gradle discover it.
tasks.named("build") {
	dependsOn(gradle.includedBuild("plugins").task(":build"))
}
