package com.lcg.mylibrary.net;

import okhttp3.Call;

/**
 * 网络数据处理器，方法都是非主线程执行。
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/14 14:04
 */

public interface ResponseHandler {
    /**
     * 请求真正开始，否则还在队列中。
     */
    void start(Call call);

    /**
     * 网络请求完成
     */
    void netFinish();

    /**
     * 请求失败，code=-1表示网络堵塞，其他表示服务器异常
     */
    void fail(int code, String errorData);

    /**
     * 请求成功
     */
    void success(String successData);
}
