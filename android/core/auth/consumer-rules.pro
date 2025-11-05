# Consumer ProGuard rules for auth module

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Keep public API
-keep public class com.familyhub.core.auth.AuthManager { *; }
-keep public class com.familyhub.core.auth.FirebaseAuthManager { *; }
