package com.lcg.comment.model

import android.app.Activity
import android.databinding.Bindable
import android.text.TextUtils
import android.view.View
import com.lcg.comment.BR
import com.lcg.comment.HttpUrl
import com.lcg.comment.activity.auth.RegisterActivity
import com.lcg.comment.bean.AuthUser
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseApplication
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.PreferenceHandler
import com.lcg.mylibrary.net.DataEntry
import com.lcg.mylibrary.utils.MD5
import com.lcg.mylibrary.utils.UIUtils
import com.lcg.mylibrary.utils.isPhone

/**
 * 登陆
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:33
 */
open class Login(activity: BaseActivity) : BaseObservableMe(activity) {
    @get:Bindable
    var username: String = ""
        set(username) {
            field = username
            notifyPropertyChanged(BR.username)
        }
    @get:Bindable
    var password: String = ""
        set(password) {
            field = password
            notifyPropertyChanged(BR.password)
        }

    fun login(view: View) {
        if (!check())
            return
        val map = hashMapOf("username" to username, "password" to MD5.GetMD5Code(password))
        DataEntry(HttpUrl.login).joinProgressDialog(activity).formBody(map).post<AuthUser> {
            BaseApplication.getInstance().token = it.token
            PreferenceHandler.getInstance().setConfigFull(it)
            try {
                val clazz: Class<Activity> = Class.forName("com.lcg.expressbus.MainActivity") as Class<Activity>
                activity.startActivity(clazz)
                activity.finish()
            } catch (e: Exception) {
                throw RuntimeException("com.lcg.expressbus.MainActivity is error!")
            }
        }
    }

    /**去注册*/
    fun gotoRegister(view: View) {
        RegisterActivity.start(activity)
    }

    /**去密码重置*/
    fun gotoResetPassword(view: View) {
        TODO()
    }

    protected open fun check(): Boolean {
        when {
            TextUtils.isEmpty(this.username) -> {
                UIUtils.showToastSafe("用户名不能为空")
                return false
            }
            !this.username.isPhone() -> {
                UIUtils.showToastSafe("请输入一个有效手机号码")
                return false
            }
            TextUtils.isEmpty(this.password) -> {
                UIUtils.showToastSafe("密码不能为空")
                return false
            }
            this.password.length < 2 -> {
                UIUtils.showToastSafe("密码必须大于两位")
                return false
            }
            else -> return true
        }
    }
}

