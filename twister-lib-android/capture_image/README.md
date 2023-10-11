# Setup

## Glide

Users of this module must ensure `net.twisterrob.android.content.glide.pooling.NonPooledBitmapModule` is included in the app's `GlideModule` list.

The easiest way to do this is to add these to the consuming app:
```gradle
plugins {
	id("com.google.devtools.ksp")
}
dependencies {
    ksp("com.github.bumptech.glide:ksp:${glideVersion}")
}
```

```kotlin
@com.bumptech.glide.annotation.GlideModule
class AppGlideModule : com.bumptech.glide.module.AppGlideModule()
```

(See how this module's androidTest source set is doing the same.)

In case it's not working, make sure you have a direct dependency on `net-twisterrob-libraries:glide4` artifact from the app module, KSP is finicky this way.
