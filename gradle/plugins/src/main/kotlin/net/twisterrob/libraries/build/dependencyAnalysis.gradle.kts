package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("com.autonomousapps.dependency-analysis")
}

// `gradlew buildHealth` or `gradle :some:module:projectHealth`
dependencyAnalysis {
	abi {
		exclusions {
			// project(":espresso_glide3")'s internal helper classes.
			ignoreSubPackage("com.bumptech.glide")
		}
	}
	issues {
		all {
			onAny {
				severity("fail")
			}
			onUsedTransitiveDependencies {
				severity("ignore")
			}
			onIncorrectConfiguration {
				// R8 needs the annotations to be on the runtime classpath too.
				exclude(libs.annotations.jsr305.get().toString())
			}
			onCompileOnly {
				// REPORT javac needs @Contract to be on the compile classpath with -Wall.
				exclude(libs.annotations.jetbrains.get().toString())
			}
			onUnusedDependencies {
				// R8 needs the annotations to be on the runtime classpath too.
				exclude(libs.annotations.jsr305.get().toString())
			}
		}
		allprojects.forEach { project ->
			if (!project.path.endsWith("-test_helpers")) return@forEach
			project(project.path) {
				onIncorrectConfiguration {
					exclude(project.path.removeSuffix("-test_helpers"))
				}
			}
		}
		project(":internal:test").subprojects.forEach { project ->
			project(project.path) {
				// Don't report dependencies of these helper projects,
				// they exist to provide these dependencies.
				onUnusedDependencies {
					severity("ignore")
				}
			}
			all {
				// Don't report usages of these helper projects,
				// they'll look like they're unused, but their transitive dependencies are needed.
				onUnusedDependencies {
					exclude(project.path)
				}
			}
		}
		project(":espresso_glide3") {
			onUnusedDependencies {
				// REPORT false positive, it is used with FQCN.
				exclude(libs.android.guava.get().toString())
			}
		}
		project(":defs") {
			onUnusedDependencies {
				// REPORT false positive, TransactionOperationCommandTest uses same-package class reference.
				exclude(libs.androidx.fragment.get().toString())
			}
		}
		project(":espresso") {
			onUnusedDependencies {
				// These dependencies are there to be provided to the consumers, keep them.
				exclude(libs.androidx.test.junit.get().toString())
			}
			onIncorrectConfiguration {
				// These dependencies are there to be provided to the consumers, keep them api.
				exclude(
					libs.androidx.test.junit.get().toString(),
					libs.androidx.test.core.get().toString(),
				)
			}
		}
		project(":internal:test:android_unit") {
			// > Advice for :internal:test:android_unit
			// > Dependencies which should be removed or changed to runtime-only:
			// > runtimeOnly("androidx.test:core:1.5.0") (was api)
			onRuntimeOnly {
				// These dependencies are there to be provided to the consumers, keep them api.
				// In particular, this lib contains androidx.test.core.app.ApplicationProvider,
				// which is the recommended way to get the application Context in Robolectric.
				exclude(libs.androidx.test.core.get().toString())
			}
		}
	}
	dependencies {
		bundle("robolectric") {
			primary("org.robolectric:robolectric")
			includeGroup("org.robolectric")
		}
	}
}
