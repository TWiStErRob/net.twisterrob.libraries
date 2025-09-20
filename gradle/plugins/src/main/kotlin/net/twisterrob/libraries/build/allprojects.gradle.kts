package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.dependencyAnalysisSub
import net.twisterrob.libraries.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

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

plugins.withId("org.jetbrains.kotlin.jvm") {
	configure<KotlinBaseExtension>(KotlinBaseExtension::configureKotlin)
}
plugins.withId("org.jetbrains.kotlin.android") {
	configure<KotlinBaseExtension>(KotlinBaseExtension::configureKotlin)
}
fun KotlinBaseExtension.configureKotlin() {
	this as HasConfigurableKotlinCompilerOptions<*>
	compilerOptions {
		allWarningsAsErrors = true

		// Kotlin 2.0: Add @ConsistentCopyVisibility to all data classes.
		// See https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-consistent-copy-visibility/
		freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")

		// Kotlin 2.1: Add warning IDs so they can be investigated/suppressed easier.
		// See https://youtrack.jetbrains.com/issue/KT-8087
		freeCompilerArgs.add("-Xrender-internal-diagnostic-names")

		// Kotlin 2.2: Enable context parameters whenever possible.
		// See https://kotlinlang.org/docs/context-parameters.html
		freeCompilerArgs.add("-Xcontext-parameters")

		// Kotlin 2.2: Opt in to future behavior.
		// > [ANNOTATION_WILL_BE_APPLIED_ALSO_TO_PROPERTY_OR_FIELD]
		// > This annotation is currently applied to the value parameter only,
		// > but in the future it will also be applied to property.
		// > - To opt in to applying to both value parameter and property,
		// >   add '-Xannotation-default-target=param-property' to your compiler arguments.
		// > - To keep applying to the value parameter only, use the '@param:' annotation target.
		// See https://kotlinlang.org/docs/whatsnew22.html#new-defaulting-rules-for-use-site-annotation-targets
		freeCompilerArgs.add("-Xannotation-default-target=param-property")
	}
	jvmToolchain {
		languageVersion = libs.versions.java.map(JavaLanguageVersion::of)
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
