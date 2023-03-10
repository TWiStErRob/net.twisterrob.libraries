plugins {
	id("net.twisterrob.libraries.root")
	@Suppress("DSL_SCOPE_VIOLATION")
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
			//onAny { severity("fail") }
			onUsedTransitiveDependencies { severity("ignore") }
		}
		project(":internal:test").subprojects.forEach { project ->
			project(project.path) {
				onUnusedDependencies { severity("ignore") }
			}
		}
		project(":capture_image") {
			onUnusedDependencies { exclude(libs.androidx.fragment.get().toString()) }
		}
		project(":espresso_glide3") {
			onUnusedDependencies { exclude(libs.androidx.fragment.get().toString()) }
		}
		(project(":lib").subprojects + project(":utils").subprojects).forEach { project ->
			project(project.path) {
				onUnusedDependencies { exclude(libs.slf4j.api.get().toString()) }
			}
		}
	}
	dependencies {
		bundle("robolectric") {
			includeGroup("org.robolectric")
		}
	}
}
