plugins {
	id("net.twisterrob.libraries.root")
}

tasks.register("connectedCheck").configure connectedCheck@{
	this@connectedCheck.dependsOn(subprojects.map { "${it.path}:connectedCheck" })
}
