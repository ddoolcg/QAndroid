package com.lcg.mylibrary.net;

import android.support.annotation.Nullable;

import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.UIUtils;

import java.io.File;

/**
 * 文件下载
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/19 17:35
 */

public abstract class FileDownloadHanler {
    /**
     * 请求真正开始，否则还在队列中。
     */
    public void start() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
    }

    /**
     * 网络请求完成
     */
    public void netFinish() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onNetFinish();
            }
        });
    }

    /**
     * 请求失败，code=-1表示网络堵塞，其他表示服务器异常
     */
    public void fail(final int code, @Nullable final Throwable t) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onFail(code, t);
            }
        });
    }

    /**
     * 请求成功。
     *
     * @param file 存储文件的位置
     */
    public void success(final File file) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onSuccess(file);
            }
        });
    }

    /**
     * 进度
     *
     * @param bytesWritten 已下载多少
     * @param totalSize    总计多少
     */
    public void progress(final long bytesWritten, final long totalSize) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onProgress(bytesWritten, totalSize);
            }
        });
    }

    /**
     * <b>“主线程执行”</b><br/>请求真正开始，否则还在队列中。
     */
    protected void onStart() {
    }

    /**
     * <b>“主线程执行”</b><br/>网络请求完成
     */
    protected void onNetFinish() {
    }

    /**
     * <b>“主线程执行”</b><br/>请求失败，code=-1表示网络堵塞，其他表示服务器异常
     */
    protected abstract void onFail(int code, @Nullable Throwable t);

    /**
     * <b>“主线程执行”</b><br/>请求成功。
     *
     * @param file 存储文件的位置
     */
    protected abstract void onSuccess(File file);

    /**
     * <b>“主线程执行”</b><br/>进度
     *
     * @param bytesWritten 已下载多少
     * @param totalSize    总计多少
     */
    protected void onProgress(long bytesWritten, long totalSize) {
        L.i("file download progress bytesWritten=" + bytesWritten + " totalSize=" + totalSize);
    }
}
