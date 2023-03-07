package com.lcg.mylibrary;

import android.app.Application;

/**
 * BaseApplication
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:07
 */

public abstract class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QAndroid.INSTANCE
                .initToken("token", showToast -> {
                    gotoLoin(showToast);
                    return null;
                })
                .setTranslucentStatusTheme(false)
                .initUIUtils(this);
    }

    /**
     * 去登陆
     */
    public abstract void gotoLoin(boolean showToast);
}
