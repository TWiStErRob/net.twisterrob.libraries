package net.twisterrob.libraries.build

plugins {
	id("org.gradle.maven-publish")
}

project.afterEvaluate {
	if (!project.hasProperty("release")) { // -Prelease to build a release version
		version = "${version}-SNAPSHOT"
	}
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
