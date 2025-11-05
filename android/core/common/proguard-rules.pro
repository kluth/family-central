# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep common exceptions
-keep class com.familyhub.core.common.exception.** { *; }

# Keep Result sealed class
-keep class com.familyhub.core.common.result.Result { *; }
-keep class com.familyhub.core.common.result.Result$Success { *; }
-keep class com.familyhub.core.common.result.Result$Error { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Timber
-dontwarn org.jetbrains.annotations.**
