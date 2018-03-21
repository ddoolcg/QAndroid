# QAndroid
简单小巧、快速的android app开发框架。基于dataBinding的mvvm架构、使用kotlin开发跟迅捷。
框架已经集成okhttp、glide、fastjson和友盟统计。

# 联网调用
DataEntry("url").joinProgressDialog(activity).formBody(map).post<T> {TODO()}

# 页面实现
activity:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/activity/auth/LoginActivity.kt

xml:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/res/layout/activity_login.xml

view model:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/model/Login.kt

# 开启友盟统计
在AndroidManifest.xml文件中修改 <meta-data android:name="UMENG_APPKEY" android:value="您的UMENG_APPKEY"/>
