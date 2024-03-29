package com.lcg.mylibrary.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.model.AlertDialogObservable

/**弹窗*/
class DialogFragment : androidx.fragment.app.DialogFragment() {
    var binding: ViewDataBinding? = null

    @LayoutRes
    var layoutId: Int = 0
    var variableId: Int = 0
    var variable: BaseObservableMe? = null
        set(value) {
            field = value
            if (value is LifecycleObserver) {
                lifecycle.addObserver(value)
            }
            binding?.setVariable(variableId, value)
        }
    var isCanceledOnTouchOutside: Boolean = true
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
            isCanceledOnTouchOutside = savedInstanceState.getBoolean(
                "android:isCanceledOnTouchOutside",
                isCanceledOnTouchOutside
            )
        layoutId = arguments?.getInt("layoutId") ?: 0
        variableId = arguments?.getInt("variableId") ?: 0
        variable?.also {
            if (it is LifecycleObserver) {
                lifecycle.addObserver(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (showsDialog) dialog?.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding!!.setVariable(variableId, variable)
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!isCanceledOnTouchOutside) {
            outState.putBoolean("android:isCanceledOnTouchOutside", isCanceledOnTouchOutside)
        }
    }

    /**显示对话框，显示之前需要给variable赋值*/
    fun show(activity: BaseActivity) {
        try {
            super.show(activity.supportFragmentManager, variable?.titleText ?: "")
        } catch (_: Exception) {
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(@LayoutRes layoutId: Int, variableId: Int) = DialogFragment().apply {
            val bundle = Bundle()
            bundle.putInt("layoutId", layoutId)
            bundle.putInt("variableId", variableId)
            arguments = bundle
        }
    }
}

/**新建一个通知弹窗*/
fun newAlert(alert: AlertDialogObservable.() -> Unit): DialogFragment {
    val dialog =
        DialogFragment.newInstance(AlertDialogObservable.layoutId, AlertDialogObservable.variableId)
    val observable = AlertDialogObservable(null, dialog)
    alert.invoke(observable)
    dialog.variable = observable
    return dialog
}