package com.lcg.mylibrary.model

import android.databinding.Bindable
import android.view.View
import com.android.databinding.library.baseAdapters.BR
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.fragment.DialogFragment

/**
 * TipDialog
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/10/24 15:23
 */
class AlertDialogObservable(activity: BaseActivity?, private val dialog: DialogFragment) : BaseObservableMe(activity) {
    var background = 0XFFFFFFFF
    var textColor = 0XFF000000

    @get:Bindable
    var title: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }
    @get:Bindable
    var message = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.message)
        }
    private var negative: String? = null
    private var positive: String? = null
    private var negativeListener: ((dialog: DialogFragment) -> Unit)? = null
    private var positiveListener: ((dialog: DialogFragment) -> Unit)? = null

    val singleString = when {
        positive?.isNotEmpty() == true -> positive!!
        negative?.isNotEmpty() == true -> negative!!
        else -> "知道了"
    }

    fun setNegative(negative: String? = "取消", clickListener: ((dialog: DialogFragment) -> Unit)? = null) {
        this.negative = negative
        this.negativeListener = clickListener
        notifyPropertyChanged(BR.negative)
    }

    fun setPositive(positive: String? = "确定", clickListener: ((dialog: DialogFragment) -> Unit)? = null) {
        this.positive = positive
        this.positiveListener = clickListener
        notifyPropertyChanged(BR.positive)
    }

    @Bindable
    fun getNegative(): String? {
        return negative
    }

    @Bindable
    fun getPositive(): String? {
        return positive
    }

    fun negative(v: View) {
        if (negativeListener == null)
            dialog.dismiss()
        else
            negativeListener!!.invoke(dialog)
    }

    fun positive(v: View) {
        if (positiveListener == null)
            dialog.dismiss()
        else
            positiveListener!!.invoke(dialog)
    }

    fun single(v: View) {
        when {
            positive?.isNotEmpty() == true -> positive(v)
            negative?.isNotEmpty() == true -> negative(v)
            else -> dialog.dismiss()
        }
    }
}
