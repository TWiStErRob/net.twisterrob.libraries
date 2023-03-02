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
		mavenLocal {
			if (System.getProperty("user.name") == "TWiStEr") {
				url = file("p:\\repos\\maven").toURI()
			}
		}
	}
}

tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}

apply(from = rootProject.file("gradle/substitutions.gradle"), to = project)
