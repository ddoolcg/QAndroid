package com.lcg.mylibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.os.Process;

import com.lcg.mylibrary.utils.PreferenceKTX;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * BaseApplication
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:07
 */

public abstract class BaseApplication extends Application {
    public final static String TOKEN = "token";
    public long mThreadId;
    private static BaseApplication instance;
    private String token;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        initCrashHandler();
        super.onCreate();
        instance = this;
        mThreadId = Thread.currentThread().getId();
        initMainProcesses();
    }

    /**
     * 异常奔溃的信息处理器初始化
     */
    private void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler
                .getInstance(getApplicationContext());
        // 注册crashHandler
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    /**
     * 主进程初始化
     */
    public void initMainProcesses() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            if (info.pid == myPid) {
                if (!info.processName.contains(":")) {
                    MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
                    MobclickAgent.enableEncrypt(true);
                    MobclickAgent.setCatchUncaughtExceptions(false);
                    // 发送上一次没有发送的异常
                    CrashHandler
                            .getInstance(getApplicationContext()).sendPreviousReportsToServer();
                    onInitMainProcesses();
                }
                break;
            }
        }
    }

    /**
     * 认证的token
     */
    public String getToken() {
        if (token == null) {
            token = PreferenceKTX.getString(TOKEN, "");
        }
        return token;
    }

    /**
     * 设置认证的token
     */
    public void setToken(String token) {
        if (token == null) {
            token = "";
        }
        this.token = token;
        PreferenceKTX.setString(TOKEN, token);
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
