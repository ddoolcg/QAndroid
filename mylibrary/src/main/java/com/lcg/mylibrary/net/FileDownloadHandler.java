package com.lcg.mylibrary.net;

import android.support.annotation.Nullable;

import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.UIUtils;

import java.io.File;

/**
 * 文件下载
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 2.0
 * @since 2021/03/08 16:13
 */
public abstract class FileDownloadHandler {
    private final boolean sync;

    public FileDownloadHandler() {
        this(false);
    }

    public FileDownloadHandler(boolean sync) {
        this.sync = sync;
    }

    /**
     * 网络请求完成
     */
    public void netFinish() {
        if (sync) {
            onNetFinish();
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    onNetFinish();
                }
            });
        }
    }

    /**
     * 请求失败，code=-1表示网络堵塞，其他表示服务器异常
     */
    public void fail(final int code, @Nullable final Throwable t) {
        if (sync) {
            onFail(code, t);
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    onFail(code, t);
                }
            });
        }
    }

    /**
     * 请求成功。
     *
     * @param file 存储文件的位置
     */
    public void success(final File file) {
        if (sync) {
            onSuccess(file);
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    onSuccess(file);
                }
            });
        }
    }

    /**
     * 进度
     *
     * @param bytesWritten 已下载多少
     * @param totalSize    总计多少
     */
    public void progress(final long bytesWritten, final long totalSize) {
        if (sync) {
            onProgress(bytesWritten, totalSize);
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    onProgress(bytesWritten, totalSize);
                }
            });
        }
    }

    /**
     * 网络请求完成
     */
    protected void onNetFinish() {
    }

    /**
     * 请求失败，code=-1表示网络堵塞，其他表示服务器异常
     */
    protected void onFail(int code, @Nullable Throwable t) {
    }

    ;

    /**
     * 请求成功。
     *
     * @param file 存储文件的位置
     */
    protected abstract void onSuccess(File file);

    /**
     * 下载进度
     *
     * @param bytesWritten 已下载多少
     * @param totalSize    总计多少
     */
    protected void onProgress(long bytesWritten, long totalSize) {
        L.i("file download progress bytesWritten=" + bytesWritten + " totalSize=" + totalSize);
    }
}
