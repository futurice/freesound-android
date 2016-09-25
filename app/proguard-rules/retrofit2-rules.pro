# Retrofit 2 rules from https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-retrofit2.pro
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep the pojos and enums used by GSON
-keep class com.futurice.freesound.network.api.model.** { *; }
