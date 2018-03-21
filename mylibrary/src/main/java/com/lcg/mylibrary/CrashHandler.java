package com.lcg.mylibrary;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lcg.mylibrary.bean.ExceptionLog;
import com.lcg.mylibrary.net.DataHandler;
import com.lcg.mylibrary.net.HttpManager;
import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.MD5;
import com.lcg.mylibrary.utils.ThreadPoolUntil;
import com.lcg.mylibrary.utils.UIUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 异常退出信息收集类
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * Debug LogUntil tag
     */
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = false;
    private static UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE;
    private static Context mContext;
    private Map<String, String> mDeviceCrashInfo = new HashMap<String, String>();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    private static final String CRASH_REPORTER_EXTENSION = ".log";
    public static String APPLOGS;

    private CrashHandler() {
        mailInit();
    }

    public static CrashHandler getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        return INSTANCE;
    }

    private void mailInit() {
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            L.w(TAG, "handleException --- ex==null");
            return true;
        }
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        // 发送错误报告到服务器
        // 优化注释掉
        // sendCrashReportsToServer(mContext);
        return true;
    }

    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    private void sendCrashReportsToServer(final Context ctx) {
        ThreadPoolUntil.getInstance().run(new Runnable() {
            @Override
            public void run() {
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
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim() + "<br/>");
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
        if (!TextUtils.isEmpty(APPLOGS)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("toemail", "leichuguang@xuebaedu.com");
            params.put("title", file.getName());
            int ver = 0;
            try {
                ver = Integer.valueOf(mDeviceCrashInfo.get(VERSION_CODE));
            } catch (Exception e) {
            }
            params.put("app_name", UIUtils.getContext().getPackageName());
            params.put("ver", ver + "");
            params.put("content", msg);
            HttpManager.getInstance().post(APPLOGS, params,
                    new DataHandler() {
                        @Override
                        public void start() {
                        }

                        @Override
                        public void netFinish() {
                        }

                        @Override
                        public void fail(int code, String errorData) {
                        }

                        @Override
                        public void success(int code, String successData) {
                            file.delete();
                        }
                    });
        }
    }

    private String saveCrashInfoToFile(Throwable ex) {
        StringWriter info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        String result = info.toString().replace("\n\t", "<br/>");
        printWriter.close();
        String localizedMessage = ex.getLocalizedMessage();
        localizedMessage = (localizedMessage == null) ? "null"
                : localizedMessage;
        String fileNameString = MD5.GetMD5Code(result
                + mDeviceCrashInfo.get(VERSION_CODE));
        String fileName = fileNameString + CRASH_REPORTER_EXTENSION;
        String[] fileList = mContext.fileList();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].equals(fileName)) {
                return fileName;
            }
        }
        ExceptionLog log = new ExceptionLog();
        initLog(result, localizedMessage, fileNameString, log);
        try {
            FileOutputStream trace = mContext.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            trace.write(JSON.toJSONBytes(log));
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            L.e(TAG, "an error occured while writing report file..." + e);
        }
        return null;
    }

    /**
     * 初始化log对象的数据
     */
    private void initLog(String result, String localizedMessage,
                         String fileNameString, ExceptionLog log) {
        log.setExceptionname(fileNameString);
        log.setException(localizedMessage);
        log.setDevicename(mDeviceCrashInfo.get("MODEL"));
        log.setVersion(mDeviceCrashInfo.get(VERSION_NAME));

        Set<String> keySet = mDeviceCrashInfo.keySet();
        StringBuffer sb = new StringBuffer();
        sb.append(STACK_TRACE + ":<br/>" + result + "<br/>");
        for (String key : keySet) {
            sb.append(key + ":" + mDeviceCrashInfo.get(key) + "<br/>");
        }
        log.setContent(sb.toString());
    }

    public void collectCrashDeviceInfo(Context ctx) {
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
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
                if (DEBUG) {
                    L.d(TAG, field.getName() + " : " + field.get(null));
                }
            } catch (Exception e) {
                L.e(TAG, "Error while collect crash info" + e);
            }
        }
    }

}
