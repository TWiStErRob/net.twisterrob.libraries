plugins {
	id("net.twisterrob.libraries.root")
	alias(libs.plugins.dependencyAnalysis)
}

// :plugins:check is not automatically invoked when doing `gradlew check`. Help Gradle discover it.
tasks.register("check") {
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

// :plugins:build is not automatically invoked when doing `gradlew build`. Help Gradle discover it.
tasks.register("build") {
	dependsOn(gradle.includedBuild("plugins").task(":build"))
}

dependencyAnalysis {
	issues {
		all {
			onAny {
				severity("fail")
			}
			onUsedTransitiveDependencies {
				severity("ignore")
			}
		}
	}
	dependencies {
		bundle("robolectric") {
			includeGroup("org.robolectric")
		}
	}
}
