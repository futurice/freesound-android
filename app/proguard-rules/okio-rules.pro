# Okio rules from https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okio.pro
#-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
