import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint

plugins {
	id("org.gradle.java-platform")
}

group = "net.twisterrob.libraries.build"

dependencies {
	constraints {
		apiWithKtx(libs.androidx.activity)
		apiWithKtx(libs.androidx.annotation)
		apiWithKtx(libs.androidx.annotationExperimental)
		apiWithKtx(libs.androidx.appcompat)
		apiWithKtx(libs.androidx.appcompatResources)
		apiWithKtx(libs.androidx.archCoreCommon)
		apiWithKtx(libs.androidx.archCoreRuntime)
		apiWithKtx(libs.androidx.broadcast)
		apiWithKtx(libs.androidx.cardview)
		apiWithKtx(libs.androidx.collection)
		apiWithKtx(libs.androidx.core)
		apiWithKtx(libs.androidx.customview)
		apiWithKtx(libs.androidx.drawerlayout)
		apiWithKtx(libs.androidx.exif)
		apiWithKtx(libs.androidx.fragment)
		apiWithKtx(libs.androidx.lifecycleCommon)
		apiWithKtx(libs.androidx.lifecycleRuntime)
		apiWithKtx(libs.androidx.lifecycleViewModel)
		apiWithKtx(libs.androidx.loader)
		apiWithKtx(libs.androidx.material)
		apiWithKtx(libs.androidx.multidex)
		apiWithKtx(libs.androidx.preference)
		apiWithKtx(libs.androidx.recyclerview)
		apiWithKtx(libs.androidx.savedstate)
		apiWithKtx(libs.androidx.swiperefreshlayout)
		apiWithKtx(libs.androidx.transition)

		apiWithKtx(libs.test.androidx.fragment)
		apiWithKtx(libs.test.androidx.core)
		apiWithKtx(libs.test.androidx.runner)
		apiWithKtx(libs.test.androidx.rules)
		apiWithKtx(libs.test.androidx.junit)
		apiWithKtx(libs.test.androidx.uiautomator)

		apiWithKtx(libs.test.androidx.espressoCore)
		apiWithKtx(libs.test.androidx.espressoIdle)
		apiWithKtx(libs.test.androidx.espressoIntents)
		apiWithKtx(libs.test.androidx.espressoContrib)
		apiWithKtx(libs.test.androidx.espressoWeb)
		apiWithKtx(libs.test.androidx.espressoAccessibility)
		apiWithKtx(libs.test.androidx.espressoConcurrent)
		apiWithKtx(libs.test.androidx.espressoNet)
	}
}

fun DependencyConstraintHandler.apiWithKtx(constraintNotation: Provider<MinimalExternalModuleDependency>) {
	api(constraintNotation) // { version { strictly(version!!) } }
	api(constraintNotation.ktx)
}

val Provider<MinimalExternalModuleDependency>.ktx: Provider<MinimalExternalModuleDependency>
	get() = this.map { it.ktx }

val MinimalExternalModuleDependency.ktx: MinimalExternalModuleDependency
	get() = DefaultMinimalDependency(
		DefaultModuleIdentifier.newId(this.module.group, "${this.module.name}-ktx"),
		DefaultMutableVersionConstraint(this.versionConstraint)
	)