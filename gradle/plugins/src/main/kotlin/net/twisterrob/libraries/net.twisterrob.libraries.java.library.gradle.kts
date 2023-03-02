import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.java-library")
}

repositories {
	mavenCentral()
}

configurations.configureEach {
	if (this.name == "lintClassPath") return@configureEach
//		this.resolutionStrategy.failOnVersionConflict()
}

apply(from = rootProject.file("twister-lib-java/gradle/substitutions.gradle"))

dependencies {
	//noinspection ForeignDelegate it's confusing but works.
	apply(from = rootProject.file("twister-lib-java/gradle/testCompile.gradle"), to = project)

	api(libs.slf4j.api)
	testImplementation(libs.slf4j.simple)
	// TODO change to implementation (first step: exclude in Android)
	api(libs.annotations.jsr305)
}
