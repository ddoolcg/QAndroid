package com.lcg.comment

import android.content.Intent
import com.lcg.comment.activity.auth.LoginActivity
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseApplication
import com.lcg.mylibrary.CrashHandler
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.UIUtils
import com.lcg.mylibrary.utils.saveToken

/**
 * MyApplication
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/3/19 15:29
 */
class MyApplication : BaseApplication() {
    override fun onInitMainProcesses() {
        L.DEBUG = BuildConfig.DEBUG
        CrashHandler.APPLOGS = "https://ddoolcg.pythonanywhere.com/error"
    }

    override fun gotoLoin(showToast: Boolean) {
        saveToken(null)
        val context = UIUtils.getContext()
        var flag = true
        for (activity in BaseActivity.activities) {
            if (activity !is LoginActivity)
                activity.finish()
            else
                flag = false
        }
        if (flag) {
            if (showToast)
                UIUtils.showToastSafe("你的账号正在其他设备上使用，请重新登录")
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

}