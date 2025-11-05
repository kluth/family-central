# Add project specific ProGuard rules here.

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.auth.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep all auth classes
-keep class com.familyhub.core.auth.** { *; }
