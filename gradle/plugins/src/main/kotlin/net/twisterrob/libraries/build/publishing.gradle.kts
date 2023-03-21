package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("org.gradle.maven-publish")
}

group = "net.twisterrob"
version = libs.versions.project.get()

// -Prelease to build a release version
if (!project.hasProperty("release")) {
	version = "${version}-SNAPSHOT"
}

publishing {
	repositories {
		maven {
			// gradlew publishAllPublicationsToLocalRepository, not publishToMavenLocal!
			name = "local"
			url = File(rootDir, "build/localMaven").toURI()
		}
	}
}

tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}
