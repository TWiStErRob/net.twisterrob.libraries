import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.java-library")
	// Must be applied after java-library, because it eagerly looks up sourceSets.main.
	id("net.twisterrob.libraries.build.java.lint")
}

repositories {
	google()
	mavenCentral()
}

configurations.configureEach {
	if (this.name == "lintClassPath") return@configureEach
//		this.resolutionStrategy.failOnVersionConflict()
}

apply(from = rootDir.resolve("gradle/substitutions.gradle"))

dependencies {
	compileOnly(libs.annotations.jsr305)
	implementation(libs.slf4j.api)
	testCompileOnly(libs.annotations.jsr305)
	testImplementation(project(":internal:test:jvm_unit"))
}
