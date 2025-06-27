# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.syntax.Syntax$$Lambda/0x* {
    final kotlin.jvm.functions.Function1 arg$1;
}

# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.ContainerHost$intent$* {
    final kotlin.jvm.functions.Function2 $transformer;
}

# See net.twisterrob.orbit.logging.LoggingContainerDecoratorKt#captured
-keepclassmembernames class org.orbitmvi.orbit.ContainerHost$blockingIntent$* {
    final kotlin.jvm.functions.Function2 $transformer;
}
