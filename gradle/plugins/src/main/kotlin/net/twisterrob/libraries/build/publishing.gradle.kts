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
		// publishAllPublicationsToLocalRepository, not publishToMavenLocal!
		maven {
			name = "local"
			url = File(rootDir, "build/repo").toURI()
		}
	}
}

tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}

/*
android.publishing { singleVariant("release") }
publishing.publications {
	release<MavenPublication> {
		afterEvaluate { from(components.release) }
		groupId = "net.twisterrob.libraries"
		artifactId = "mylibrary"
		version = "1.0"
		versionMapping {
			usage("java-api") {
				fromResolutionOf("releaseRuntimeClasspath")
			}
			usage("java-runtime") {
				fromResolutionResult()
			}
		}
	}
}
*/
