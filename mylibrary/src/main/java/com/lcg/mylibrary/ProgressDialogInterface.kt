package com.lcg.mylibrary

import okhttp3.Call

/**
 * 进度对话框接口
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/12/26 10:52
 */
interface ProgressDialogInterface {
    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    fun showProgressDialog(msg: String = "加载中...", call: Call? = null, cancelable: Boolean = true)

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    fun dismissProgressDialog(msg: String? = null)
}