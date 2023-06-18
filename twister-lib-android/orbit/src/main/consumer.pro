# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.syntax.simple.SimpleSyntaxExtensionsKt$reduce$* {
    final kotlin.jvm.functions.Function1 $reducer;
}

# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.syntax.simple.SimpleSyntaxExtensionsKt$intent$* {
    final kotlin.jvm.functions.Function2 $transformer;
}

# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.syntax.simple.SimpleSyntaxExtensionsKt$blockingIntent$* {
    final kotlin.jvm.functions.Function2 $transformer;
}
