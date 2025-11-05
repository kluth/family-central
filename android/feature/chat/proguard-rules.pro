# Add project specific ProGuard rules here.

# Keep ViewModels
-keep class com.familyhub.feature.chat.presentation.** { *; }

# Keep navigation args
-keep class com.familyhub.feature.chat.navigation.** { *; }

# Hilt
-dontwarn com.google.errorprone.annotations.*
