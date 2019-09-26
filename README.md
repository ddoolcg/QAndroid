
# QAndroid
简单小巧、快速的android app开发框架。基于dataBinding的mvvm架构、使用kotlin开发跟迅捷。
框架已经集成okhttp、glide、fastjson和友盟统计。

# 关于使用
~~~gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
~~~
~~~gradle
dependencies {
    api 'com.github.ddoolcg:QAndroid:1.5.1'
}
~~~

# UIUtils工具类需要初始化、自定义http认证，参考BaseApplication实现
~~~java
@Override
public void onCreate() {
     super.onCreate();
     if (UIUtils.init(this)) onInitMainProcesses();
     Token.INSTANCE.init("token", new Function1<Boolean, Unit>() {
         @Override
         public Unit invoke(Boolean aBoolean) {
              gotoLoin(aBoolean);
              return null;
          }
      });
  }
~~~

# 联网调用
~~~kotlin
//lambda不支持泛型套泛型的解析方式
DataEntry("url").joinProgressDialog(activity).formBody(map).post<T> {TODO()}
//抽象方法实现的方式调用，支持泛型套泛型的解析方式
DataEntry("url").joinProgressDialog(activity).formBody(map).post<T>(OnSuccessListener)
//默认统一处理接口调用失败
DataEntry.failDefault={code,data->
    TODO()
    false
}
~~~

# SharedPreferences操作
## 操作类
~~~kotlin
PreferenceKTX
~~~
## 多次put采用Any扩展
~~~kotlin
preferenceEdit {
    putBoolean()
    putString()
}
~~~

# view的一些实用扩展
## doOnGlobalLayout
~~~kotlin
view.doOnGlobalLayout {
    if (Boolean) {
        action()
        true
    } else {
        false
    }
}
~~~
## doOnPreDraw
~~~kotlin
view.doOnPreDraw {
    action()
}
~~~
## ViewGroup
~~~kotlin
viewGroup += view//addView
viewGroup -= view//removeView
~~~

# List简单实现
## Fragment
基于dataBinding的实现的ListFragment，函数说明：
|name              | 说明|
|----------------- | -------------|
|loadData          | 装载数据列表，会清除掉历史数据|
|addData           | 添加数据列表，不会清除掉历史数据|
|removeItem        | 移除一条数据|
|filter            | 过滤，需要复写item的toString()，多个关键字以空格分隔|

## RecyclerView.Adapter
基于dataBinding的实现的CommentAdapter
~~~kotlin
CommentAdapter(items, R.layout.item_demo, BR.item)
~~~
构造函数说明：
|name              | 说明|
|----------------- | -------------|
|data              | 数据列表|
|mLayoutId         | layout布局|
|mVariableId       | BR ID，也就是xml的data|
|mFooterViewHolder | 底部的加载更多，为null表示无该功能|

# 页面实现demo
activity:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/activity/auth/LoginActivity.kt

xml:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/res/layout/activity_login.xml

model:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/model/Login.kt

## ListActivity
activity:
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/ListActivity.kt
model:
https://github.com/ddoolcg/QAndroid/blob/master/app/src/main/java/com/lcg/expressbus/model/ListViewModelDemo.kt
调用：
~~~kotlin
val bundle = Bundle().apply {
    putString("id", "my id")
}
ListActivity.start(this@MainActivity, ListViewModelDemo::class.java, bundle)
~~~

# 开启友盟统计
在AndroidManifest.xml文件中修改 
~~~xml
<meta-data android:name="UMENG_APPKEY" android:value="您的UMENG_APPKEY"/>
~~~

# DataEntry无法满足你的需求可参考：
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/net/Base200DataHandler.java
https://github.com/ddoolcg/QAndroid/blob/master/comment/src/main/java/com/lcg/comment/net/MyDataEntry.kt
