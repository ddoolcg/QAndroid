package com.lcg.mylibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.lcg.mylibrary.net.HttpManager;
import com.lcg.mylibrary.net.ResponseHandler;
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

import okhttp3.Call;

/**
 * 异常退出信息收集类
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String CRASH_REPORTER_EXTENSION = ".log";
    private static CrashHandler INSTANCE;
    private final Application mContext;
    private final String mUrl;
    private final Attach mAttach;
    //
    private final String packageName;
    private String versionName = "未知";
    private int versionCode = -1;

    private CrashHandler(Application ctx, String url, Attach attach) {
        mContext = ctx;
        mUrl = url;
        mAttach = attach;
        packageName = ctx.getPackageName();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (Exception ignored) {
        }
    }

    public static CrashHandler getInstance(Application ctx, String url, Attach attach) {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler(ctx, url, attach);
        }
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex)) {// 如果用户没有处理则让系统默认的异常处理器来处理
            UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
            if (handler != null) handler.uncaughtException(thread, ex);
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
        if (TextUtils.isEmpty(mUrl)) {
            return false;
        } else {
            saveCrashInfoToFile(ex);
            return true;
        }
    }

    /**
     * 保存错误报告文件
     */
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
        String fileNameString = MD5.GetMD5Code(sbTag.toString() + versionCode);
        String fileName = fileNameString + CRASH_REPORTER_EXTENSION;
        String[] fileList = mContext.fileList();
        for (String s : fileList) {
            if (s.equals(fileName)) {
                return;
            }
        }
        //保存文件
        if (mAttach != null) sb.append(mAttach.onAttach());
        try {
            FileOutputStream trace = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            trace.write(sb.toString().getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送之前的异常
     */
    public void sendPreviousReportsToServer() {
        ThreadPoolUntil.getInstance().run(() -> {
            final String[] crFiles = getCrashReportFiles();
            if (crFiles != null && crFiles.length > 0) {
                try {
                    for (String fileName : crFiles) {
                        File cr = new File(mContext.getFilesDir(), fileName);
                        postReport(cr);
                        // cr.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String[] getCrashReportFiles() {
        File filesDir = mContext.getFilesDir();
        FilenameFilter filter = (dir, name) -> name.endsWith(CRASH_REPORTER_EXTENSION);
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
            params.put("app_name", packageName);
            params.put("version_code", versionCode + "");
            params.put("version_name", versionName);
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

    interface Attach {
        String onAttach();
    }
}
