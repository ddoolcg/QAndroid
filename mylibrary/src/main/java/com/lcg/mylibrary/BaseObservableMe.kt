package com.lcg.mylibrary

import android.app.Activity
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.alibaba.fastjson.annotation.JSONType
import com.lcg.mylibrary.utils.L
import okhttp3.Call
import java.io.Serializable

/**
 * databinding 的 BaseObservable基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 2.0
 * @since 2019/12/26 111:11
 */
@JSONType(ignores = ["activity", "leftText", "titleText", "rightText", "showBack"])
open class BaseObservableMe(val activity: BaseActivity? = null) : BaseObservable(), Serializable, ProgressDialogInterface {
    @get:Bindable
    var leftText = "返回"
        set(value) {
            field = value
            notifyPropertyChanged(BR.leftText)
        }

    @get:Bindable
    var titleText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.titleText)
        }

    @get:Bindable
    var rightText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.rightText)
        }

    @get:Bindable
    var showBack = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.showBack)
        }

    fun clickLeft(v: View?) {
        activity?.finish()
    }

    fun clickRight(v: View?) {
        L.i("右键被点击")
    }

    /**
     * 开启一个activity
     */
    protected fun startActivity(clazz: Class<out Activity>) {
        activity?.startActivity(clazz)
    }

    override fun showProgressDialog(msg: String, call: Call?, cancelable: Boolean) {
        activity?.showProgressDialog(msg, call, cancelable)
    }

    override fun dismissProgressDialog(msg: String?) {
        activity?.dismissProgressDialog(msg)
    }
}