package com.lcg.mylibrary.model

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.Bindable
import com.lcg.mylibrary.BR
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.R
import com.lcg.mylibrary.fragment.DialogFragment

/**
 * TipDialog
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/10/24 15:23
 */
class AlertDialogObservable(activity: BaseActivity?, private val dialog: DialogFragment) : BaseObservableMe(activity){
    val background = AlertDialogObservable.background
    val textColor = AlertDialogObservable.textColor

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

    @get:Bindable
    var negative: String? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.negative)
            notifyPropertyChanged(BR.singleString)
        }

    @get:Bindable
    var positive: String? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.positive)
            notifyPropertyChanged(BR.singleString)
        }
    private var negativeListener: ((dialog: DialogFragment) -> Unit)? = null
    private var positiveListener: ((dialog: DialogFragment) -> Unit)? = null

    @get:Bindable
    val singleString: String
        get() = when {
            positive?.isNotEmpty() == true -> positive!!
            negative?.isNotEmpty() == true -> negative!!
            else -> "知道了"
        }

    fun setNegative(negative: String? = "取消", clickListener: ((dialog: DialogFragment) -> Unit)? = null) {
        this.negative = negative
        this.negativeListener = clickListener
    }

    fun setPositive(positive: String? = "确定", clickListener: ((dialog: DialogFragment) -> Unit)? = null) {
        this.positive = positive
        this.positiveListener = clickListener
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
    fun onDismiss(listener: ((dialog: DialogFragment) -> Unit)? = null){

    }
    companion object {
        var background: Int = -0x1
        var textColor: Int = -0x1000000

        @LayoutRes
        var layoutId: Int = R.layout.dialog_alert
        var variableId: Int = BR.dialog
    }
}
