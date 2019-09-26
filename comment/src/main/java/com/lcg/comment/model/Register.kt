package com.lcg.comment.model

import android.app.Activity
import android.databinding.Bindable
import android.text.TextUtils
import android.view.View
import com.android.databinding.library.baseAdapters.BR
import com.lcg.comment.HttpUrl
import com.lcg.comment.activity.auth.LoginActivity
import com.lcg.comment.bean.AuthUser
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.net.DataEntry
import com.lcg.mylibrary.utils.MD5
import com.lcg.mylibrary.utils.PreferenceKTX
import com.lcg.mylibrary.utils.UIUtils
import com.lcg.mylibrary.utils.saveToken

/**
 * 注册
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 15:57
 */

class Register(activity: BaseActivity) : Login(activity) {
    @get:Bindable
    var repassword: String = ""
        set(repassword) {
            field = repassword
            notifyPropertyChanged(BR.repassword)
        }

    fun register(view: View) {
        if (!check())
            return
        val map = hashMapOf("username" to username, "password" to MD5.GetMD5Code(password))
        DataEntry(HttpUrl.register).joinProgressDialog(activity).formBody(map).post<AuthUser> {
            saveToken(it.token)
            PreferenceKTX.setConfig(it)
            try {
                val clazz: Class<Activity> = Class.forName("com.lcg.expressbus.MainActivity") as Class<Activity>
                activity.startActivity(clazz)
                activity.finish()
            } catch (e: Exception) {
                throw RuntimeException("com.lcg.expressbus.MainActivity is error!")
            }
            BaseActivity.activities.forEach {
                (it as? LoginActivity)?.finish()
            }
        }
    }

    override fun check(): Boolean {
        val check = super.check()
        val password = password
        if (!check) {
            return false
        } else if (TextUtils.isEmpty(this.repassword)) {
            UIUtils.showToastSafe("重复密码不能为空")
            return false
        } else if (password != this.repassword) {
            UIUtils.showToastSafe("两次密码不一致")
            return false
        }
        return true
    }
}
