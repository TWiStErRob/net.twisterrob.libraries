import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("net.twisterrob.libraries.build.allprojects")
	id("net.twisterrob.libraries.build.publishing")
	id("net.twisterrob.gradle.plugin.java-library")
	// Must be applied after java-library, because it eagerly looks up sourceSets.main.
	id("net.twisterrob.libraries.build.java.lint")
}

repositories {
	google()
	mavenCentral()
}

dependencies {
	implementation(platform("net.twisterrob.libraries.build:platform-libs"))
	testImplementation(platform("net.twisterrob.libraries.build:platform-libs"))
}

dependencies {
	implementation(libs.annotations.jsr305)
	compileOnly(libs.slf4j.api)
	testImplementation(project(":internal:test:jvm_unit"))
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}
