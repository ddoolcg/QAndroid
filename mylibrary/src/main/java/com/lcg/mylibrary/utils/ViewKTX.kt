package com.lcg.mylibrary.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * View的一些扩展
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/3/22 10:33
 */
/**通过DataBindingUtil去setContentView
 * @see[DataBindingUtil.setContentView]*/
fun <T : ViewDataBinding> Activity.setContentViewBinding(layoutResID: Int): T {
    return DataBindingUtil.setContentView(this, layoutResID)
}

/**通过DataBindingUtil去inflate
 * @see[DataBindingUtil.inflate]*/
@JvmOverloads
fun <T : ViewDataBinding> LayoutInflater.inflateBinding(
    @LayoutRes layoutResID: Int,
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): T {
    return DataBindingUtil.inflate(this, layoutResID, parent, attachToParent)
}