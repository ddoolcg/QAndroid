package com.lcg.mylibrary;

import android.app.Application;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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
                .initToken("token", new Function1<Boolean, Unit>() {
                    @Override
                    public Unit invoke(Boolean showToast) {
                        gotoLoin(showToast);
                        return null;
                    }
                })
                .setTranslucentStatusTheme(false)
                .initUIUtils(this, new Function1<Boolean, Unit>() {
                    @Override
                    public Unit invoke(Boolean main) {
                        if (main) onInitMainProcesses();
                        return null;
                    }
                });
    }

    /**
     * 被主进程初始化调用
     */
    protected abstract void onInitMainProcesses();

    /**
     * 去登陆
     */
    public abstract void gotoLoin(boolean showToast);
}
