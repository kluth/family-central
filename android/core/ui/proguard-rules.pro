# Add project specific ProGuard rules here.

# Keep Compose components
-keep class com.familyhub.core.ui.components.** { *; }
-keep class com.familyhub.core.ui.theme.** { *; }

# Compose specific rules
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
