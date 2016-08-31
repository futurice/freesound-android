-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}
-keepattributes Signature
-dontwarn retrofit.**

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Keep the pojos and enums used by GSON
-keep class com.futurice.freesound.network.api.model.** { *; }
