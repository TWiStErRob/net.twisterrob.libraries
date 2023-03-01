rootProject.name = "twister-libs-java"

pluginManagement {
	includeBuild("../gradle/plugins")
	repositories {
		google()
		mavenCentral()
	}
}

include(":")
include(":utils:stringer")
include(":utils:core")
include(":utils:collect")
include(":utils:test")
include(":lib:general")
include(":lib:java")
include(":lib:java_desktop")
include(":lib:junit4")
include(":lib:hamcrest")
include(":lib:mockito")
