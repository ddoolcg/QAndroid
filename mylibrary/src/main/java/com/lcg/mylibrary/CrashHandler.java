package com.lcg.mylibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lcg.mylibrary.net.HttpManager;
import com.lcg.mylibrary.net.ResponseHandler;
import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.MD5;
import com.lcg.mylibrary.utils.ThreadPoolUntil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;

/**
 * 异常退出信息收集类
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static CrashHandler INSTANCE;
    /**
     * Debug LogUntil tag
     */
    private static final String TAG = "CrashHandler";
    private final UncaughtExceptionHandler mDefaultHandler;
    private final Application mContext;
    private final Map<String, String> mDeviceCrashInfo = new HashMap<>();

    private static String mUrl;
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String APP_NAME = "appName";
    private static final String CRASH_REPORTER_EXTENSION = ".log";

    private CrashHandler(Application ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance(Application ctx, String url) {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler(ctx);
        }
        mUrl = url;
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //下面代码解决：activity创建生命周期时奔溃导致无限重启，在创建中的时候奔溃会把上一个activity关闭
            ArrayList<Activity> activities = BaseActivity.getActivities();
            if (!activities.isEmpty()) {
                Activity activity = activities.get(activities.size() - 1);
                activity.finish();
            }
            //
            android.os.Process.killProcess(android.os.Process.myPid());
            //虚拟机退出这段代码没什么用
//            System.exit(0);
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            L.w(TAG, "handleException --- ex==null");
            return true;
        }
        if (TextUtils.isEmpty(mUrl)) {
            L.w(TAG, "handleException --- mUrl==null");
            return true;
        }
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        // 发送错误报告到服务器
        // sendCrashReportsToServer(mContext);
        // 尝试等待发送报告结束
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return true;
    }

    /**
     * 发送之前的异常
     */
    public synchronized void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    /**
     * 发送异常
     */
    private void sendCrashReportsToServer(final Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            String packageName = ctx.getPackageName();
            mDeviceCrashInfo.put(APP_NAME, packageName);
            PackageInfo pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
            }
        } catch (Exception ignored) {
        }
        ThreadPoolUntil.getInstance().run(() -> {
            final String[] crFiles = getCrashReportFiles(ctx);
            if (crFiles != null && crFiles.length > 0) {
                try {
                    for (String fileName : crFiles) {
                        File cr = new File(ctx.getFilesDir(), fileName);
                        postReport(cr);
                        // cr.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    private void postReport(File file) {
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim()).append("<br/>");
            }
            reader.close();
            sendMsg(file, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息实体
     */
    private void sendMsg(final File file, String msg) {
        if (!TextUtils.isEmpty(mUrl)) {
            HashMap<String, String> params = new HashMap<>();
//            params.put("toemail", "475825657@qq.com");
            params.put("title", file.getName());
            params.put("app_name", mDeviceCrashInfo.get(APP_NAME));
            params.put("version_code", mDeviceCrashInfo.get(VERSION_CODE));
            params.put("version_name", mDeviceCrashInfo.get(VERSION_NAME));
            params.put("content", msg);
            HttpManager.getInstance().post(mUrl, params,
                    new ResponseHandler() {

                        @Override
                        public void start(Call call) {
                        }

                        @Override
                        public void netFinish() {
                        }

                        @Override
                        public void fail(int code, String errorData) {
                        }

                        @Override
                        public void success(String successData) {
                            file.delete();
                        }
                    });
        }
    }

    private void saveCrashInfoToFile(Throwable ex) {
        final StringBuilder sbTag = new StringBuilder();
        StringWriter info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info) {
            @Override
            public void println(@Nullable String x) {
                super.println(x);
                if (x != null)
                    add(x);
            }

            @Override
            public void println(@Nullable Object x) {
                super.println(x);
                if (x != null)
                    add(x.toString());
            }

            private void add(String s) {
                if (!s.startsWith("\tat android") && !s.startsWith("\tat com.android.") && !s.startsWith("\tat java")) {
                    sbTag.append(s);
                }
            }
        };
        ex.printStackTrace(printWriter);
        StringBuffer sb = info.getBuffer();
        printWriter.close();
        //缓存到文件规则
        String fileNameString = MD5.GetMD5Code(sbTag + mDeviceCrashInfo.get(VERSION_CODE));
        String fileName = fileNameString + CRASH_REPORTER_EXTENSION;
        String[] fileList = mContext.fileList();
        for (String s : fileList) {
            if (s.equals(fileName)) {
                return;
            }
        }
        //保存文件
        Set<String> keySet = mDeviceCrashInfo.keySet();
        for (String key : keySet) {
            sb.append(key).append(":").append(mDeviceCrashInfo.get(key)).append("<br/>");
        }
        try {
            FileOutputStream trace = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            trace.write(sb.toString().replace("\t", "<br/>").getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            L.e(TAG, "an error occured while writing report file..." + e);
        }
    }

    private void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            L.e(TAG, "Error while collect package info" + e);
        }
        //保留有效设备信息
        mDeviceCrashInfo.put("TIME", Build.TIME + "");
        mDeviceCrashInfo.put("FINGERPRINT", Build.FINGERPRINT + "");
        mDeviceCrashInfo.put("DEVICE", Build.MANUFACTURER + "/" + Build.MODEL + "/" + Build.DEVICE + " os=" + Build.VERSION.SDK_INT);
        mDeviceCrashInfo.put("CPU_ABI", Build.CPU_ABI + " " + Build.CPU_ABI2);
    }

}
