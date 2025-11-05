# Add project specific ProGuard rules here.

# Keep ViewModels
-keep class com.familyhub.feature.profile.presentation.** { *; }

# Keep navigation args
-keep class com.familyhub.feature.profile.navigation.** { *; }

# Hilt
-dontwarn com.google.errorprone.annotations.*
