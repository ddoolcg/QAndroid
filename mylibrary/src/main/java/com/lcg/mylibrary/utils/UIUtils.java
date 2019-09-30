package com.lcg.mylibrary.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hjq.toast.ToastUtils;
import com.lcg.mylibrary.CrashHandler;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class UIUtils {
    private static Handler handler;
    private static DisplayMetrics sMetrics;
    private static PackageInfo pi;
    private static Thread sMainThread;
    private static Application application;

    /**
     * 初始化
     */
    public static boolean init(Application app) {
        sMainThread = Thread.currentThread();
        application = app;
        ToastUtils.init(app, MyToastStyle.INSTANCE);
        //异常奔溃的信息处理器初始化
        CrashHandler crashHandler = CrashHandler
                .getInstance(app);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        //
        return initMainProcesses(app);
    }

    /**
     * 在主进程初始化友盟统计，发送奔溃日志的服务
     */
    private static boolean initMainProcesses(Application app) {
        ActivityManager am = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            if (info.pid == myPid) {
                if (!info.processName.contains(":")) {
                    MobclickAgent.setScenarioType(app, MobclickAgent.EScenarioType.E_UM_NORMAL);
                    MobclickAgent.enableEncrypt(true);
                    MobclickAgent.setCatchUncaughtExceptions(false);
                    // 发送上一次没有发送的异常
                    CrashHandler.getInstance(app.getApplicationContext())
                            .sendPreviousReportsToServer();
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public static Context getContext() {
        return application;
    }

    /**
     * 获取主线程
     *
     * @see #getMainThreadId()
     * @deprecated 请勿对主线程做set相关操作。
     */
    @Deprecated
    public static Thread getMainThread() {
        return sMainThread;
    }

    /**
     * 获取主线程ID
     */
    public static long getMainThreadId() {
        return sMainThread.getId();
    }

    /**
     * 生成并得到DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics() {
        if (sMetrics == null) {
            sMetrics = getContext().getResources().getDisplayMetrics();
        }
        return sMetrics;
    }

    /**
     * 获取宽度的总DP
     */
    public static int getWidthDp() {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (metrics.widthPixels / metrics.density);
    }

    /**
     * 获取屏幕的宽度
     */
    public static int getWidth() {
        DisplayMetrics metrics = getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public static int getHeight() {
        DisplayMetrics metrics = getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * 获取屏幕高的总DP
     */
    public static int getHeightDp() {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (metrics.heightPixels / metrics.density);
    }

    /**
     * dip转换px
     */
    public static int dip2px(float dip) {
        DisplayMetrics metrics = getDisplayMetrics();
        final float scale = metrics.density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static float px2dip(int px) {
        DisplayMetrics metrics = getDisplayMetrics();
        final float scale = metrics.density;
        return px / scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(float spValue) {
        DisplayMetrics metrics = getDisplayMetrics();
        final float scale = metrics.scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        // 获取主线程的handler
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
        return handler;
    }

    /**
     * 延时在主线程执行runnable
     */
    public static boolean postDelayed(long delayMillis, Runnable runnable) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 获取颜色选择器
     */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    public static boolean isRunInMainThread() {
        return Thread.currentThread() == sMainThread;
    }

    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final int resId) {
        showToastSafe(getString(resId));
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final String str) {
        if (isRunInMainThread()) {
            showToast(str);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showToast(str);
                }
            });
        }
    }

    private static void showToast(String str) {
        ToastUtils.show(str);
    }

    /**
     * 隐藏输入法
     */
    public static void hideInputMethodManager(Activity context) {
        if (context.getCurrentFocus() != null
                && context.getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(context.getCurrentFocus()
                            .getWindowToken(), 0);
        }
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager
                        .getApplicationInfo(ctx.getPackageName(),
                                PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取我的PackageInfo
     */
    public static PackageInfo getPackageInfo() {
        if (pi == null)
            try {
                PackageManager pm = UIUtils.getContext().getPackageManager();
                String packageName = UIUtils.getContext().getPackageName();
                pi = pm.getPackageInfo(packageName,
                        PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
            }
        return pi;
    }
}
