-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings 
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }  
-keep public class * extends android.support.v4.**  
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.app.Activity
-keep public class * extends android.view.View
-keepattributes *Annotation*
-keepattributes Signature
-keep class **.bean.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers  class * extends android.app.Activity {
   public void *(android.view.View);
   <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}