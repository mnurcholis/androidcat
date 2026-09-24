# ProGuard rules for CAT Android App (optimizes size to < 8MB)
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.cat.androidcat.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
