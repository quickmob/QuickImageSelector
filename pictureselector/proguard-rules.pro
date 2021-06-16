# 基础混淆
-keep class com.lookballs.imageselector.** { *; }
-dontwarn com.lookballs.imageselector.**
# subscaleview混淆
-keep class com.davemorrissey.labs.subscaleview.** {*;}
-dontwarn com.davemorrissey.labs.subscaleview.**
# glide混淆
# https://proguard-rules.blogspot.com/2017/05/glide-proguard-rules.html
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
# https://github.com/bumptech/glide/blob/master/library/proguard-rules.txt
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# greenrobot混淆
-keep class org.greenrobot.eventbus.** {*;}
-dontwarn org.greenrobot.eventbus.**
# photoview混淆
-keep class com.github.chrisbanes.photoview.** {*;}
-dontwarn com.github.chrisbanes.photoview.**
# utilcode混淆
-keep class com.blankj.utilcode.** {*;}
-dontwarn com.blankj.utilcode.**