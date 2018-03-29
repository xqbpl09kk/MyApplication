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
# hide the original source outputFile name.
#-renamesourcefileattribute SourceFile


# Retain generic type information for use by reflection by converters and adapters

# Retain service method parameters



-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# umeng push
-dontwarn org.android.agoo.control.**
-dontwarn com.taobao.tlog.adapter.**
-dontwarn com.taobao.accs.**
-dontwarn anetwork.channel.**
-dontwarn anet.channel.**
-dontwarn com.huawei.android.**




#-keep class retrofit2.** { *; }
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class fast.information.network.bean.**{ *; }
-keep class com.umeng.commonsdk.** {*;}

#-keepattributes Signature
#-keepattributes Exceptions
#-keepattributes Signature
#-keepattributes *Annotation*


#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}

