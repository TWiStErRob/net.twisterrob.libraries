dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		exclusiveContent {
			forRepository {
				maven("https://oss.sonatype.org/service/local/repositories/snapshots/content/")
			}
			filter {
				includeModule("com.autonomousapps", "dependency-analysis-gradle-plugin")
			}
		}
	}
	versionCatalogs {
		create("libs") {
			from(files("../../gradle/libs.versions.toml"))
		}
	}
}
