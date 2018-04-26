package com.lcg.mylibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.lcg.mylibrary.dialog.ProgressDialog
import com.umeng.analytics.MobclickAgent
import okhttp3.Call
import java.util.*

/**
 * 所有activity的基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:03
 */

open class BaseActivity : FragmentActivity() {
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activities.add(this)
    }

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        dismissProgressDialog(null)
        activities.remove(this)
        super.onDestroy()
    }

    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    @JvmOverloads
    fun showProgressDialog(msg: String, call: Call, cancelable: Boolean = true) {
        if (mProgressDialog == null)
            mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(cancelable)
        mProgressDialog!!.setCall(call)
        mProgressDialog!!.show(msg)
    }

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    fun dismissProgressDialog(msg: String?) {
        if (mProgressDialog != null) {
            try {
                mProgressDialog!!.dismiss(msg)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 去另外一个页面
     */
    fun startActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    fun back(v: View) {
        finish()
    }

    companion object {
        @JvmStatic
        var activities = ArrayList<BaseActivity>()
    }
}
