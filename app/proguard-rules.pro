# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.google.errorprone.**
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}
-keep class dagger.hilt.** { *; }
-keep class **_HiltModules { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# ---------- Kotlin / Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**


# ---------- Keep line numbers for crash reports (optional; comment out to save a few KB) ----------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile