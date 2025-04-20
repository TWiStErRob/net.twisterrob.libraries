package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.dependencyAnalysisSub
import net.twisterrob.libraries.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.autonomousapps.dependency-analysis")
}

configurations.configureEach {
	resolutionStrategy {
		failOnChangingVersions()
		failOnDynamicVersions()
		failOnNonReproducibleResolution()
		//failOnVersionConflict()
	}
}

configurations.configureEach {
	resolutionStrategy {
		apply(from = rootDir.resolve("gradle/settings.substitutions.gradle"), to = this)
		dependencySubstitution {
			substitute(module(libs.deprecated.hamcrestCore.get().module.toString()))
				.using(module(libs.test.hamcrest.asProvider().get().toString()))
			substitute(module(libs.deprecated.hamcrestLibrary.get().module.toString()))
				.using(module(libs.test.hamcrest.asProvider().get().toString()))
		}
	}
}

tasks.withType<JavaCompile>().configureEach javac@{
	this@javac.options.compilerArgs = this@javac.options.compilerArgs + listOf(
		// Enable all warnings the compiler knows.
		"-Xlint:all",
		// Fail build when any warning pops up.
		"-Werror",
	)
}

tasks.withType<KotlinCompile>().configureEach kotlin@{
	compilerOptions {
		allWarningsAsErrors.set(true)
		jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
		freeCompilerArgs.add("-Xcontext-receivers")
	}
}

afterEvaluate {
	// Have to apply it later, otherwise Test.javaVersion locks in value,
	// before JavaBasePlugin has a chance to set up the convention.
	tasks.withType<Test>().configureEach test@{
		if (javaVersion.isCompatibleWith(JavaVersion.VERSION_1_9)
			&& !javaVersion.isCompatibleWith(JavaVersion.VERSION_17)
		) { // 9 <= Java < 17
			jvmArgs(
				"--illegal-access=deny",
			)
		}
	}
}

tasks.withType<Test>().configureEach test@{
	systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
	jvmArgs(
		// Reduce occurrences of warning:
		// > Java HotSpot(TM) 64-Bit Server VM warning:
		// > Sharing is only supported for boot loader classes because bootstrap classpath has been appended
		"-Xshare:off",
	)
}

dependencyAnalysisSub {
	issues {
		// There are some configuration in root project's issues.all { ... } block. 

		if (project.path.endsWith("-test_helpers")) {
			val targetProject = project.path.removeSuffix("-test_helpers")
			onIncorrectConfiguration {
				exclude(targetProject)
			}
			onUnusedDependencies {
				exclude(targetProject)
			}
		}
		onUnusedDependencies {
			// Don't report usages of these helper projects,
			// they'll look like they're unused, but their transitive dependencies are needed.
			project(":internal:test").subprojects.forEach { project ->
				exclude(project.path)
			}
		}
	}
}
