plugins {
	id("net.twisterrob.libraries.build.allprojects")
	id("net.twisterrob.libraries.container")
	id("net.twisterrob.gradle.plugin.quality")
}

tasks.register("check")
tasks.register("build") {
	dependsOn("check")
}

tasks.register("cleanFull") {
	dependsOn(allprojects.map { it.tasks.named("clean") })
}

// To get gradle/dependency-locks run `gradlew :allDependencies --write-locks`.
// See also :module:dependencies and :module:androidDependencies and :module:dependencyInsight.
tasks.register<Task>("allDependencies") {
	val projects = project.allprojects.sortedBy { it.path }
	val projectPaths = "Printing dependencies for modules:\n" +
		projects.joinToString("\n") { " * ${it}" }
	doFirst {
		println(projectPaths)
	}
	val dependenciesTasks = projects.map { it.tasks.named("dependencies") }
	// Builds a dependency chain: 1 <- 2 <- 3 <- 4, so when executed they're in order.
	dependenciesTasks.reduce { acc, task -> task.apply { get().dependsOn(acc) } }
	// Use finalizedBy instead of dependsOn to make sure this task executes first.
	this@register.finalizedBy(dependenciesTasks)
}
