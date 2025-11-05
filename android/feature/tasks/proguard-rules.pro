# Add project specific ProGuard rules here.

# Keep ViewModels
-keep class com.familyhub.feature.tasks.presentation.** { *; }

# Keep navigation args
-keep class com.familyhub.feature.tasks.navigation.** { *; }

# Hilt
-dontwarn com.google.errorprone.annotations.*
