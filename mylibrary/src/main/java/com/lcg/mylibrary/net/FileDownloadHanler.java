package com.lcg.mylibrary.net;

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
    public void fail(final int code) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onFail(code);
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
     * @param file
     */
    public void progress(final long bytesWritten, final long totalSize, final File file) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onProgress(bytesWritten, totalSize, file);
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
    protected abstract void onFail(int code);

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
     * @param file
     */
    protected void onProgress(long bytesWritten, long totalSize, File file) {
        L.i("file download progress bytesWritten=" + bytesWritten + " totalSize=" + totalSize);
    }
}
