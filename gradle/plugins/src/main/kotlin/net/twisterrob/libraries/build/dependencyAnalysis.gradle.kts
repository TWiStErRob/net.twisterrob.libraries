package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.libs

plugins {
	id("com.autonomousapps.dependency-analysis")
}

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
				exclude(libs.test.androidx.junit.get().toString())
			}
			onIncorrectConfiguration {
				// These dependencies are there to be provided to the consumers, keep them api.
				exclude(
					libs.test.androidx.junit.get().toString(),
					libs.test.androidx.core.get().toString(),
				)
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
