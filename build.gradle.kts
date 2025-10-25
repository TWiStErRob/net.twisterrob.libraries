plugins {
	id("net.twisterrob.libraries.root")
	id("net.twisterrob.libraries.build.instrumentedTestReport")
	id("com.autonomousapps.dependency-analysis")
}

// :plugins:check is not automatically invoked when doing `gradlew check`. Help Gradle discover it.
tasks.named("check") {
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

// :plugins:build is not automatically invoked when doing `gradlew build`. Help Gradle discover it.
tasks.named("build") {
	dependsOn(gradle.includedBuild("plugins").task(":build"))
}

// `gradlew buildHealth` or `gradle :some:module:projectHealth`
dependencyAnalysis {
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
				exclude(libs.annotations.jsr305)
			}
			onCompileOnly {
				// REPORT javac needs @Contract to be on the compile classpath with -Wall.
				exclude(libs.annotations.jetbrains)
			}
			onUnusedDependencies {
				// R8 needs the annotations to be on the runtime classpath too.
				exclude(libs.annotations.jsr305)
			}
		}
	}
	structure {
		// https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/wiki/Customizing-plugin-behavior#ktx-dependencies
		ignoreKtx(true)
		bundle("robolectric") {
			primary("org.robolectric:robolectric")
			includeGroup("org.robolectric")
		}
	}
}
