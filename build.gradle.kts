plugins {
	id("net.twisterrob.libraries.root")
}

tasks.register("connectedCheck") {
	dependsOn(subprojects.map { "${it.path}:connectedCheck" })
}

tasks.register("check") {
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

tasks.register("build") {
	dependsOn(gradle.includedBuild("plugins").task(":build"))
}
