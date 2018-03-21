package com.lcg.mylibrary.utils;

import android.util.Log;

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
        FileUtils.save(UIUtils.getContext().getPackageName() + ".log", msg, true);
    }

    public static void file(Throwable error) {
        StringWriter info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        error.printStackTrace(printWriter);
        String result = info.toString();
        printWriter.close();
        file(error.toString() + "\n\n" + result + "\n\n\n\n");
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
