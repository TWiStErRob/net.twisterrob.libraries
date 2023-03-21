import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint

plugins {
	id("org.gradle.java-platform")
}

group = "net.twisterrob.libraries.build"

dependencies {
	javaPlatform.allowDependencies()
	// Lock in the version of Kotlin used so that the transitive dependencies are consistently upgraded.
	// https://kotlinlang.org/docs/whatsnew18.html#usage-of-the-latest-kotlin-stdlib-version-in-transitive-dependencies
	api(platform(libs.kotlin.bom))

	constraints {
		apiWithKtx(libs.androidx.activity)
		apiWithKtx(libs.androidx.annotation)
		apiWithKtx(libs.androidx.annotationExperimental)
		apiWithKtx(libs.androidx.appcompat)
		apiWithKtx(libs.androidx.appcompatResources)
		apiWithKtx(libs.androidx.archCoreCommon)
		apiWithKtx(libs.androidx.archCoreRuntime)
		apiWithKtx(libs.androidx.cardview)
		apiWithKtx(libs.androidx.collection)
		apiWithKtx(libs.androidx.core)
		apiWithKtx(libs.androidx.customview)
		apiWithKtx(libs.androidx.drawerlayout)
		apiWithKtx(libs.androidx.exifinterface)
		apiWithKtx(libs.androidx.fragment)
		apiWithKtx(libs.androidx.lifecycleCommon)
		apiWithKtx(libs.androidx.lifecycleRuntime)
		apiWithKtx(libs.androidx.lifecycleViewModel)
		apiWithKtx(libs.androidx.loader)
		apiWithKtx(libs.androidx.localbroadcastmanager)
		apiWithKtx(libs.androidx.material)
		apiWithKtx(libs.androidx.multidex)
		apiWithKtx(libs.androidx.preference)
		apiWithKtx(libs.androidx.recyclerview)
		apiWithKtx(libs.androidx.savedstate)
		apiWithKtx(libs.androidx.swiperefreshlayout)
		apiWithKtx(libs.androidx.tracing)
		apiWithKtx(libs.androidx.transition)

		apiWithKtx(libs.androidx.test.fragment)
		apiWithKtx(libs.androidx.test.core)
		apiWithKtx(libs.androidx.test.runner)
		apiWithKtx(libs.androidx.test.rules)
		apiWithKtx(libs.androidx.test.junit)
		apiWithKtx(libs.androidx.test.monitor)
		apiWithKtx(libs.androidx.test.uiautomator)

		apiWithKtx(libs.androidx.test.espressoCore)
		apiWithKtx(libs.androidx.test.espressoIdling)
		apiWithKtx(libs.androidx.test.espressoIntents)
		apiWithKtx(libs.androidx.test.espressoContrib)
		apiWithKtx(libs.androidx.test.espressoWeb)
		apiWithKtx(libs.androidx.test.espressoAccessibility)
		apiWithKtx(libs.androidx.test.espressoConcurrent)
		apiWithKtx(libs.androidx.test.espressoNet)
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
