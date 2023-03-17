package net.twisterrob.libraries.build

import net.twisterrob.libraries.build.dsl.libs

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

tasks.withType<Test>().configureEach test@{
	if (javaVersion.isCompatibleWith(JavaVersion.VERSION_1_9)
		&& !javaVersion.isCompatibleWith(JavaVersion.VERSION_17)) { // 9 <= Java < 17
		jvmArgs(
			"--illegal-access=deny",
			// PowerMock eagerly calls setAccessible on EVERYTHING 😂.
			// > WARNING: Illegal reflective access by org.powermock.reflect.internal.WhiteboxImpl
			// > to method java.lang.Throwable.*
			// > to method java.lang.Integer.*
			// > to method java.lang.String.*
			// > to method java.lang.Object.*
			// at org.powermock.reflect.internal.WhiteboxImpl.doGetAllMethods(WhiteboxImpl.java:1508)
			"--add-opens=java.base/java.lang=ALL-UNNAMED",
			"--add-opens=java.base/java.util=ALL-UNNAMED",
		)
	}
}

tasks.withType<Test>().configureEach test@{
	systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
}
