# Add project specific ProGuard rules here.

# Keep domain models (entities)
-keep class com.familyhub.core.domain.model.** { *; }

# Keep use cases
-keep class com.familyhub.core.domain.usecase.** { *; }

# Keep repository interfaces
-keep interface com.familyhub.core.domain.repository.** { *; }

# Javax inject
-keepattributes *Annotation*
-keep class javax.inject.** { *; }
-keep interface javax.inject.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
