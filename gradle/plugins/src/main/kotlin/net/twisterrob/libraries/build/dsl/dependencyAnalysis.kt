package net.twisterrob.libraries.build.dsl

import com.autonomousapps.DependencyAnalysisSubExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * Configures the [dependencyAnalysis][com.autonomousapps.DependencyAnalysisSubExtension] extension.
 */
internal fun Project.dependencyAnalysisSub(configure: Action<DependencyAnalysisSubExtension>) {
	if (this.rootProject != this) {
		(this as ExtensionAware).extensions.configure("dependencyAnalysis", configure)
	}
}
