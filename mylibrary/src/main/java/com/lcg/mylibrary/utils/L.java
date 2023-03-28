package com.lcg.mylibrary.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class L {
    private static final String TAG = "fast";
    public static boolean DEBUG = false;

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void file(String msg) {
        save(UIUtils.getContext().getPackageName() + ".log", msg, true);
    }

    /**
     * 保存string到文件fileName
     */
    public static void save(String fileName, String string, boolean append) {
        try {
            File file = new File(Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath()
                    + "/" + fileName);
            if (append && file.lastModified() < System.currentTimeMillis() - 3600000)
                append = false;
            FileWriter w = new FileWriter(file, append);
            w.write("\n--------------------------\n");
            w.write(string);
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void file(Throwable error) {
        StringWriter info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        error.printStackTrace(printWriter);
        String result = info.toString();
        printWriter.close();
        file(error + "\n\n" + result + "\n\n\n\n");
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

}
