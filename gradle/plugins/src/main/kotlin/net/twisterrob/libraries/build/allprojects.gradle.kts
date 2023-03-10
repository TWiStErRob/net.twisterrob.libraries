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
