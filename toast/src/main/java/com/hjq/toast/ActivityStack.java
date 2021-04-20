package com.hjq.toast;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2021/04/07
 *    desc   : Activity 生命周期监控
 */
final class ActivityStack implements Application.ActivityLifecycleCallbacks {

    /**
     * 注册
     */
    static ActivityStack register(Application application) {
        ActivityStack lifecycle = new ActivityStack();
        application.registerActivityLifecycleCallbacks(lifecycle);
        return lifecycle;
    }

    /** 前台 Activity 对象 */
    private Activity mForegroundActivity;

    public Activity getForegroundActivity() {
        return mForegroundActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {
        mForegroundActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mForegroundActivity != activity) {
            return;
        }
        mForegroundActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}