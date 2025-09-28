package net.twisterrob.libraries.build.dsl

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

/**
 * This is useful to emulate the `androidComponents` extension in a convention plugin
 * where the `android-library` or `android-application` are not applied, but implied.
 *
 * @see com.android.build.api.variant.ApplicationAndroidComponentsExtension
 * @see com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
 * @see com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
 * @see com.android.build.api.variant.LibraryAndroidComponentsExtension
 * @see com.android.build.api.variant.TestAndroidComponentsExtension
 */
internal val Project.androidComponents: AndroidComponentsExtension<*, *, *>
	@Suppress("UNCHECKED_CAST")
	get() = this.extensions["androidComponents"] as AndroidComponentsExtension<*, *, *>

/**
 * This is useful to emulate the `androidComponents` block in a convention plugin
 * where the `android-library` or `android-application` are not applied, but implied.
 *
 * @param block the configuration for common Android things.
 * Using an [Action] to take advantage of `kotlin-dsl`.
 */
internal fun Project.androidComponents(block: Action<AndroidComponentsExtension<*, *, *>>) {
	block.execute(androidComponents)
}
