package com.lcg.mylibrary

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.Call

/**
 * 所有fragment的基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:10
 */
open class BaseFragment : Fragment(), ProgressDialogInterface {
    protected var activity: BaseActivity? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is BaseActivity) this.activity = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textView = TextView(container!!.context)
        textView.text = "开发中..."
        return textView
    }

    override fun showProgressDialog(msg: String, call: Call?, cancelable: Boolean) {
        activity?.showProgressDialog(msg, call, cancelable)
    }

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    override fun dismissProgressDialog(msg: String?) {
        activity?.dismissProgressDialog(msg)
    }

    /**
     * 替换当前Fragment
     *
     * @param fragment
     * @param isBack         是否可回到当前Fragment
     * @param enterAnimation 为0时无动画
     * @param exitAnimation  为0时无动画
     */
    protected fun replaceFragment(
        fragment: Fragment?, isBack: Boolean,
        enterAnimation: Int, exitAnimation: Int
    ) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(
            enterAnimation, exitAnimation,
            enterAnimation, exitAnimation
        )
        transaction.replace(this.id, fragment!!)
        if (isBack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}