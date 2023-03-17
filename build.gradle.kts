plugins {
	id("net.twisterrob.libraries.root")
	id("net.twisterrob.libraries.build.dependencyAnalysis")
}

// :plugins:check is not automatically invoked when doing `gradlew check`. Help Gradle discover it.
tasks.named("check") {
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

// :plugins:build is not automatically invoked when doing `gradlew build`. Help Gradle discover it.
tasks.named("build") {
	dependsOn(gradle.includedBuild("plugins").task(":build"))
}
