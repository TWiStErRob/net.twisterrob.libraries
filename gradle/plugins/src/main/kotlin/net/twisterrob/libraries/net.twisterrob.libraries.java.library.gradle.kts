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

apply(from = rootProject.file("gradle/substitutions.gradle"))

dependencies {
	//noinspection ForeignDelegate it's confusing but works.
	apply(from = rootProject.file("gradle/testCompile.gradle"), to = project)

	@Suppress("VariableNaming")
	val VERSION_SLF4J: String by project
	api("org.slf4j:slf4j-api:${VERSION_SLF4J}")
	testImplementation("org.slf4j:slf4j-simple:${VERSION_SLF4J}")
	// TODO change to implementation (first step: exclude in Android)
	api(libs.jsr305)
}
