plugins {
	id("net.twisterrob.quality")
}

tasks.register<Delete>("clean") {
	delete(project.buildDir)
}

tasks.register("cleanFull") {
	// subprojects*.tasks*.named("clean") is not available at this point
	dependsOn(subprojects.map { "${it.path}:clean" })
	dependsOn(tasks.named("clean"))
}
