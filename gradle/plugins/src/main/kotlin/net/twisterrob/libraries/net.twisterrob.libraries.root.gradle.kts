plugins {
	id("net.twisterrob.quality")
}

tasks.register("check")
tasks.register("build") {
	dependsOn("check")
}

tasks.register<Delete>("clean") {
	delete(project.buildDir)
}

tasks.register("cleanFull") {
	// subprojects*.tasks*.named("clean") is not available at this point
	dependsOn(subprojects.map { "${it.path}:clean" })
	dependsOn(tasks.named("clean"))
}

// To get gradle/dependency-locks run `gradlew :allDependencies --write-locks`.
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
