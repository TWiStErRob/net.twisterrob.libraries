package net.twisterrob.libraries.build.dsl

import org.gradle.api.Project

internal val Project.namespace: String
	get() = this
		.path
		.removePrefix(":")
		.replace(":", ".")
		.replace("-test_helpers", ".test_helpers")
		.let { "net.twisterrob.android.${it}" }
