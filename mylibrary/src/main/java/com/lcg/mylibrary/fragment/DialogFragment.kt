package com.lcg.mylibrary.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.model.AlertDialogObservable

/**弹窗*/
class DialogFragment : android.support.v4.app.DialogFragment() {
    @LayoutRes
    var layoutId: Int = 0
    var variableId: Int = 0
    var variable: BaseObservableMe? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (variable == null) {
            dismiss()
            return null
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, container, false)
        binding.setVariable(variableId, variable)
        return binding.root
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
    val dialog = DialogFragment.newInstance(AlertDialogObservable.layoutId, AlertDialogObservable.variableId)
    val observable = AlertDialogObservable(null, dialog)
    alert.invoke(observable)
    dialog.variable = observable
    return dialog
}