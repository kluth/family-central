# Add project specific ProGuard rules here.

# Keep repository implementations
-keep class com.familyhub.core.data.repository.** { *; }

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep Firebase model classes
-keep class com.familyhub.core.data.model.** { *; }
-keep class com.familyhub.core.data.mapper.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt
-dontwarn com.google.errorprone.annotations.*
