package com.lcg.mylibrary.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lcg.mylibrary.ProgressDialogInterface
import com.lcg.mylibrary.ProgressDialogInterface.Companion.layout
import com.lcg.mylibrary.R
import okhttp3.Call

/**
 * 进度对话框
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/21 11:45
 */
class ProgressDialog(context: Context) : Dialog(context, R.style.dialog_style),
    ProgressDialogInterface {
    private var mCall: Call? = null
    private val tvMsg: TextView?

    init {
        if (context is Activity) setOwnerActivity(context)
        val view = LayoutInflater.from(context).inflate(layout, null)
        tvMsg = view.findViewById<TextView?>(R.id.tv_msg)
        setContentView(view)
        setOnCancelListener(DialogInterface.OnCancelListener { dialog: DialogInterface? ->
            if (mCall != null) {
                mCall!!.cancel()
            }
        })
    }

    fun show(msg: String?) {
        if (tvMsg != null) {
            if (TextUtils.isEmpty(msg)) {
                tvMsg.visibility = View.GONE
            } else {
                tvMsg.visibility = View.VISIBLE
                tvMsg.text = msg
            }
        }
        var b = true
        if (Build.VERSION.SDK_INT >= 17) {
            b = ownerActivity == null || !ownerActivity!!.isDestroyed
        }
        if (!isShowing && b) show()
    }

    @Throws(Exception::class)
    fun dismiss(msg: String?) {
        if (tvMsg == null || TextUtils.isEmpty(msg) || tvMsg.getText().toString() == msg) {
            dismiss()
        } else {
            throw Exception("没有<$msg>消息对话框实体")
        }
    }

    fun setCall(call: Call?) {
        mCall = call
    }

    override fun showProgressDialog(
        msg: String,
        call: Call?,
        cancelable: Boolean,
        canceledOnTouchOutside: Boolean
    ) {
        setCancelable(cancelable)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        setCall(call)
        show(msg)
    }

    override fun dismissProgressDialog(msg: String?) {
        try {
            dismiss(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
